package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Service layer for {@link Message}.
 *
 * @author Niklas Reinhard
 * @see MessageRepository
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ReactiveMongoTemplate reactiveTemplate;

    @Autowired
    public MessageService(MessageRepository messageRepository, ReactiveMongoTemplate reactiveTemplate) {
        this.messageRepository = messageRepository;
        this.reactiveTemplate = reactiveTemplate;
    }

    public Flux<Message> findAllMessages(String chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    public Flux<Message> findAllMessagesPaginated(String chatId, String start, String chunk) {
        int startDoc = Integer.parseInt(start);
        int chunkSize = Integer.parseInt(chunk);

        return messageRepository.findAllByChatId(chatId, PageRequest.of(startDoc, chunkSize));
    }

    public Mono<Message> saveMessage(Message message, String chatId) {
        // TODO get chat sender from ReactiveSecurityContextHolder, see findAllChatsForCurrentUser
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);

        return messageRepository.save(message);
    }

    // TODO @PreAuthorize
    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }

    public Flux<Message> streamNewMessages() {
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(newAggregation(match(where("operationType").is("insert"))))
                .build();

        return reactiveTemplate
                .changeStream("message", options, Message.class)
                .map(ChangeStreamEvent::getBody);
    }
}
