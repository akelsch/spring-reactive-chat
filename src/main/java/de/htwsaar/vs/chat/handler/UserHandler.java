package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 
    public static final String ID_QUERY_PARAM = "id";
    public static final String GROUP_QUERY_PARAM = "group";
    public static final String NAME_QUERY_PARAM = "name";

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.findAll().filter(u -> matchByQuery(u, request.queryParams())), User.class);
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

    public boolean matchByQuery(User u, MultiValueMap<String, String> queryParams){
        final List<String> userGroups = u.getRoles().stream().map(Object::toString).collect(Collectors.toList());
        final List<String> queryGroups = queryParams.get(GROUP_QUERY_PARAM);
        boolean b = true;

        if(queryGroups != null){
            b = userGroups.containsAll(queryGroups);
        }

        for(String key : queryParams.keySet()){
            for(String val : queryParams.get(key)){
                switch(key){
                    case ID_QUERY_PARAM:
                        b = b && u.getId().equals(val);
                        break;
                    case NAME_QUERY_PARAM:
                        b = b && u.getUsername().equals(val);
                    default:
                        continue;
                }
            }
        }
        return b;
    }
}

