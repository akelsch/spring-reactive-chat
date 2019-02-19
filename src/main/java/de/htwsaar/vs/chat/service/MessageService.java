package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Message}.
 *
 * @author Niklas Reinhard
 * @see MessageRepository
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);

        return messageRepository.save(message);
    }

    public Mono<Void> deleteMessage(String messageId) {
        return messageRepository.deleteById(messageId);
    }
}
