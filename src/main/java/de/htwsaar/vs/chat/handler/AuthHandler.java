package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.AuthRouter;
import de.htwsaar.vs.chat.service.UserService;
import de.htwsaar.vs.chat.util.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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

    private final UserService userService;

    @Autowired
    public AuthHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request
                .bodyToMono(User.class)
                .flatMap(userService::save)
                .flatMap(user -> ServerResponse.created(URI.create("/api/v1/users/" + user.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }

    public Mono<ServerResponse> signin(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
