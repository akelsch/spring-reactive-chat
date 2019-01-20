package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.AuthRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
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

    private final UserService userService;

    @Autowired
    public AuthHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request
                .bodyToMono(User.class)
                .flatMap(userService::registerUser)
                .flatMap(user -> ServerResponse.created(URI.create("/users/" + user.getId())).build())
                .onErrorResume(AuthHandler::handleError);
    }

    public Mono<ServerResponse> signin(ServerRequest request) {
        // TODO implement JWT token authentication
        return null;
    }

    private static Mono<? extends ServerResponse> handleError(Throwable e) {
        if (e instanceof DecodingException | e instanceof ConstraintViolationException)
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()));
        else if (e instanceof DuplicateKeyException)
            return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
        else
            return Mono.error(e);
    }
}
