package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.AuthRouter;
import de.htwsaar.vs.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Handler methods for {@link AuthRouter}.
 *
 * @author Arthur Kelsch
 */
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final UserService userService;

    public Mono<ServerResponse> signup(ServerRequest request) {
        return request
                .bodyToMono(User.class)
                .flatMap(userService::createUser)
                .flatMap(user -> {
                    URI uri = UriComponentsBuilder.fromUriString("/api/v1/users/{uid}").build(user.getId());
                    return ServerResponse.created(uri).build();
                });
    }

    public Mono<ServerResponse> signin(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
