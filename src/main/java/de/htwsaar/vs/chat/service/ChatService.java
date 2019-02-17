package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for {@link Chat}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
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
        return chatRepository.findAllByMembers(userId);
    }

    public Flux<User> findAllMembersForChat(String chatId) {
        return chatRepository
                .findById(chatId)
                .flatMapMany(chat -> Flux.fromIterable(chat.getMembers()));
    }

    @PostAuthorize("@webSecurity.addChatAuthority(authentication, #chat)")
    public Mono<Chat> save(Chat chat) {
        return chatRepository.save(chat);
    }

    public Mono<Chat> saveNewMember(String chatId, User member) {
        // todo validate if user has permission to add new admin user to chat
        return chatRepository.findById(chatId)
                .doOnNext(chat -> chat.getMembers().add(member))
                .flatMap(chatRepository::save);
    }

    public Mono<Void> removeMember(String chatId, String userId, String principalId) {
        return chatRepository
                .findById(chatId)
                .filter(chat -> userId.equals(principalId) || isGroupAdmin(chat, principalId))
                .filter(chat -> chat.getMembers().removeIf(member -> member.getId().equals(userId)))
                .flatMap(chatRepository::save)
                .then();
    }

    private static boolean isGroupAdmin(Chat chat, String principalId) {
        // TODO
        /*return chat.getMembers().stream()
                .filter(member -> member.getId().equals(principalId))
                .findFirst()
                .orElse(new User())
                .getIsAdmin();*/
        return false;
    }
}
