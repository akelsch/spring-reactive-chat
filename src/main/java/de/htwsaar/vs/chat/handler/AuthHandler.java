package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.AuthRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                .flatMap(user -> ServerResponse.created(URI.create("/api/v1/users/" + user.getId())).build());
    }

    public Mono<ServerResponse> signin(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
