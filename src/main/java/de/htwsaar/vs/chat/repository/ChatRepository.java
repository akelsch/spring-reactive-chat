package de.htwsaar.vs.chat.repository;

import de.htwsaar.vs.chat.model.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Repository for {@link Chat}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, String> {

    Flux<Chat> findAllByMembers(String userId);
}
