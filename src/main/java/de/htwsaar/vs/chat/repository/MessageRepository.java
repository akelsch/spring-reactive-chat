package de.htwsaar.vs.chat.repository;

import de.htwsaar.vs.chat.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Repository for {@link Message}.
 *
 * @author Niklas Reinhard
 */
@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {

    Flux<Message> findAllByChatId(String chatId);

    Flux<Message> findAllByChatId(String chatId, Pageable pageable);
}
