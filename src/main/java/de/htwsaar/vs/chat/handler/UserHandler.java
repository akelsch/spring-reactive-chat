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
                .body(filterByQuery(request), User.class);
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

    public Flux<User> filterByQuery(ServerRequest request){
        final MultiValueMap<String, String> queryparams = request.queryParams();
        Flux<User> tmp = userService.findAll();

        if(queryparams.size() == 0){
            return tmp;
        }


        for(final String key : queryparams.keySet()){
            for(final String value : queryparams.get(key)){
                    switch(key){
                    case NAME_QUERY_PARAM:
                        tmp = tmp.filter(x -> x.getUsername().equals(value));
                        break;
                    case ID_QUERY_PARAM:
                        tmp = tmp.filter(x -> x.getId().equals(value));
                        break;
                    case GROUP_QUERY_PARAM:
                        tmp = tmp.filter(x -> {
                            Set<String> rolesAsString = new HashSet<>();
                            for(Role i : x.getRoles()){
                                rolesAsString.add(i.toString());
                            }
                            return rolesAsString.contains(value);
                        });
                        break;

                }
            }
        }

        return tmp;
    }
}