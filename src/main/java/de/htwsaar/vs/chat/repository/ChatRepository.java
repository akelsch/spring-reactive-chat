package de.htwsaar.vs.chat.repository;

import de.htwsaar.vs.chat.model.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data Repository for {@link Chat}.
 *
 * @author Niklas Reinhard
 */
@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, String> {
    Flux<Chat> findAllByMembersUserId(String id);
    Mono<Chat> findById(String id);
}