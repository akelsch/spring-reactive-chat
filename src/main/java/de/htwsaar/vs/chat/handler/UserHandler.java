package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Handler methods for {@link UserRouter}.
 *
 * @author Arthur Kelsch
 */
@Component
public class UserHandler {

    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.findAll(), User.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return userService
                .findById(request.pathVariable("uid"))
                .flatMap(user -> ServerResponse.ok().body(Mono.just(user), User.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        return ServerResponse.noContent()
                .build(userService.deleteById(request.pathVariable("uid")));
    }
}
