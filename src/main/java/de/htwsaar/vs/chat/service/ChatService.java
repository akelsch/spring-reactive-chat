package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

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
    private final ReactiveMongoOperations mongoOperations;

    @Autowired
    public ChatService(ChatRepository chatRepository, ReactiveMongoOperations mongoOperations) {
        this.chatRepository = chatRepository;
        this.mongoOperations = mongoOperations;
    }

    public Flux<Chat> findAllChatsForCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserPrincipal.class)
                .flatMapMany(principal -> chatRepository.findAllByMembers(principal.getId()));
    }

    @PostAuthorize("@webSecurity.addChatAuthority(authentication, #chat)")
    public Mono<Chat> saveChat(Chat chat) {
        Set<User> members = CollectionUtils.emptySetIfNull(chat.getMembers());

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserPrincipal.class)
                .doOnNext(principal -> members.add(principal.getUser()))
                .doOnNext(principal -> chat.setMembers(members))
                .flatMap(principal -> chatRepository.save(chat));
    }

    @PreAuthorize("@webSecurity.hasChatAuthority(authentication, #chatId) " +
            "and @webSecurity.removeChatAuthority(authentication, #chatId)")
    public Mono<Void> deleteChat(String chatId) {
        return chatRepository.deleteById(chatId);
    }

    public Flux<User> findAllMembers(String chatId) {
        return chatRepository
                .findById(chatId)
                .flatMapMany(chat -> Flux.fromIterable(chat.getMembers()));
    }

    public Mono<Chat> saveMember(String chatId, User member) {
        return chatRepository
                .findById(chatId)
                .doOnNext(chat -> chat.getMembers().add(member))
                .flatMap(chatRepository::save);
    }

    // TODO delete chat authority for userId
    @PreAuthorize("@webSecurity.hasChatAuthority(authentication, #chatId) or #userId == principal.id")
    public Mono<Void> deleteMember(String chatId, String userId) {
        return chatRepository
                .findById(chatId)
                .filter(chat -> chat.getMembers().removeIf(member -> member.getId().equals(userId)))
                .flatMap(chatRepository::save)
                .then();
    }

    public Flux<Message> streamNewMessages() {
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(newAggregation(match(where("operationType").is("insert"))))
                .build();

        return mongoOperations
                .changeStream("message", options, Message.class)
                .zipWith(findAllChatsForCurrentUser().collectList())
                .filter(tuple -> tuple.getT2().contains(Objects.requireNonNull(tuple.getT1().getBody()).getChat()))
                .map(tuple -> tuple.getT1().getBody());
    }
}
