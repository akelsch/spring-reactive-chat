package de.htwsaar.vs.chat.repository;

import de.htwsaar.vs.chat.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Repository for {@link User}.
 *
 * @author Arthur Kelsch
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByUsername(String username);
}
