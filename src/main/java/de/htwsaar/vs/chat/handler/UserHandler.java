package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Handler methods for {@link UserRouter}.
 *
 * @author Arthur Kelsch
 * @author Leslie Marxen
 */
@Component
public class UserHandler {

    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        MultiValueMap<String, String> queryParams = request.queryParams();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.findAll().filter(matchByQueryParams(queryParams)), User.class);
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

    private static Predicate<User> matchByQueryParams(MultiValueMap<String, String> queryParams) {
        Predicate<User> predicate = u -> true;

        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            switch (key) {
                case "username":
                    predicate = predicate.and(u -> {
                        String username = u.getUsername();
                        return username.equals(values.get(0));
                    });
                    break;
                case "roles":
                    predicate = predicate.and(u -> {
                        List<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toList());
                        return roles.containsAll(values);
                    });
                    break;
            }
        }

        return predicate;
    }
}
