package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Message}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 * @author Mahan Karimi
 * @see MessageRepository
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Mono<Message> findById(String messageId) {
        return messageRepository.findById(messageId);
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

        return SecurityUtils.getPrincipal()
                .doOnNext(principal -> message.setSender(principal.user()))
                .flatMap(principal -> messageRepository.save(message));
    }

    @PreAuthorize("@webSecurity.isMessageSender(authentication, #message)")
    public Mono<Void> deleteMessage(Message message) {
        return messageRepository.delete(message);
    }
}
