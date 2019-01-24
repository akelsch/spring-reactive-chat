package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Chat}.
 *
 * @author Niklas Reinhard
 * @see UserRepository
 */
@Service
public class ChatService {
    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository){
        this.chatRepository = chatRepository;
    }

    public Flux<Chat> findAllForUser(String userId){
        return chatRepository.findAllByMemberIdsContains(userId);
    }

    public Mono<Chat> save(Chat chat){
        return chatRepository.save(chat);
    }

}
