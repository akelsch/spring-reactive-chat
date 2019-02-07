package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Chat}.
 *
 * @author Niklas Reinhard
 * @see ChatRepository
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Flux<Chat> findAllForUser(String userId) {
        return chatRepository.findAllByMembersUserId(userId);
    }

    public Flux<Chat.Member> findAllMembersForChat(String chatId) {
        return chatRepository
                .findById(chatId)
                .flatMapMany(chat -> Flux.fromIterable(chat.getMembers()));
    }

    public Mono<Chat> save(Chat chat) {
        return chatRepository.save(chat);
    }

    public Mono<Chat> saveNewMember(String chatId, Chat.Member member) {
        // todo validate if user has permission to add new admin user to chat
        Mono<Chat> modifiedChat = chatRepository.findById(chatId);

        return modifiedChat
                .map(chat -> {
                    // only add member if not already exists
                    // unfortunately no possibility to do this directly with mongo
                    if (!chat.getMembers().contains(member)) {
                        chat.getMembers().add(member);
                    }
                    return chat;
                })
                .flatMap(chatRepository::save);
    }

    public Mono<Void> removeMember(String chatId, String userId) {
        // todo check if current user has permissions to delete other members
        return chatRepository
                .findById(chatId)
                .filter(chat -> chat.getMembers().removeIf(member -> member.getUser().getId().equals(userId)))
                .flatMap(chatRepository::save)
                .then();
    }
}
