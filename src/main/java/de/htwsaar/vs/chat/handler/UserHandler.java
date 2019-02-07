package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.Password;
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
 * @author Mahan Karimi
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
        String uid = request.pathVariable("uid");

        return userService
                .findById(uid)
                .flatMap(user -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(user), User.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String uid = request.pathVariable("uid");

        return userService
                .deleteById(uid)
                .flatMap(signal -> ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> changePassword(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<Password> password = request.bodyToMono(Password.class);

        return userService
                .findById(uid)
                .zipWith(password)
                // TODO passwordEncoder.matches()
                //.filter(x -> x.getT1().getPassword().equals(x.getT2().getOldPassword()))
                .flatMap(x -> {
                    User user = x.getT1();
                    user.setPassword(x.getT2().getNewPassword());
                    return userService.updated(user);
                })
                .flatMap(u -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
