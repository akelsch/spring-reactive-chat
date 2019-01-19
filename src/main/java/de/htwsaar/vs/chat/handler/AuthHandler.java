package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import de.htwsaar.vs.chat.router.AuthRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.net.URI;

/**
 * Handler methods for {@link AuthRouter}.
 *
 * @author Arthur Kelsch
 */
@Component
public class AuthHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request
                .bodyToMono(User.class)
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(user.getPassword())))
                .flatMap(userRepository::save)
                .flatMap(user -> ServerResponse.created(URI.create("/users/" + user.getId())).build())
                .onErrorResume(e -> {
                    if (e instanceof ConstraintViolationException)
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()));
                    else if (e instanceof DuplicateKeyException)
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
                    else
                        return Mono.error(e);
                });
    }

    public Mono<ServerResponse> signin(ServerRequest request) {
        // TODO implement
        return null;
    }
}
