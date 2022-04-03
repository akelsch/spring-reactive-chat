package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.model.user.PasswordRequest;
import de.htwsaar.vs.chat.model.user.RoleRequest;
import de.htwsaar.vs.chat.model.user.StatusRequest;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import de.htwsaar.vs.chat.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

/**
 * Handler methods for {@link UserRouter}.
 *
 * @author Arthur Kelsch
 * @author Mahan Karimi
 * @author Leslie Marxen
 * @author Julian Quint
 */
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserService userService;
    private final ValidationService validationService;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        MultiValueMap<String, String> queryParams = request.queryParams();

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll().filter(matchByQueryParams(queryParams)), User.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        String uid = request.pathVariable("uid");

        return userService
                .findById(uid)
                .flatMap(user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(user), User.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String uid = request.pathVariable("uid");

        return userService
                .deleteById(uid)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> changePassword(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<PasswordRequest> password = request
                .bodyToMono(PasswordRequest.class)
                .doOnNext(validationService::validateObject);

        return userService
                .findById(uid)
                .zipWith(password)
                .flatMap(tuple -> userService.updatePassword(tuple.getT1(), tuple.getT2()))
                .flatMap(user -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> putRole(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<GrantedAuthority> role = request
                .bodyToMono(RoleRequest.class)
                .doOnNext(validationService::validateObject)
                .map(RoleRequest::getRole)
                .map(SimpleGrantedAuthority::new);

        return userService
                .findById(uid)
                .zipWith(role)
                .doOnNext(tuple -> tuple.getT1().addRole(tuple.getT2()))
                .flatMap(tuple -> userService.updateRoles(tuple.getT1()))
                .flatMap(user -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteRole(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<GrantedAuthority> role = request
                .bodyToMono(RoleRequest.class)
                .doOnNext(validationService::validateObject)
                .map(RoleRequest::getRole)
                .map(SimpleGrantedAuthority::new);

        return userService
                .findById(uid)
                .zipWith(role)
                .filter(tuple -> tuple.getT1().removeRole(tuple.getT2()))
                .flatMap(tuple -> userService.updateRoles(tuple.getT1()))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> putStatus(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<StatusRequest> status = request
                .bodyToMono(StatusRequest.class)
                .doOnNext(validationService::validateObject);

        return userService
                .findById(uid)
                .zipWith(status)
                .doOnNext(tuple -> tuple.getT1().setStatus(tuple.getT2().getStatus()))
                .flatMap(tuple -> userService.updateStatus(tuple.getT1()))
                .flatMap(user -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteStatus(ServerRequest request) {
        String uid = request.pathVariable("uid");

        return userService
                .findById(uid)
                .doOnNext(user -> user.setStatus(""))
                .flatMap(userService::updateStatus)
                .then(ServerResponse.noContent().build());
    }

    private static Predicate<User> matchByQueryParams(MultiValueMap<String, String> queryParams) {
        Predicate<User> predicate = user -> true;

        for (String key : queryParams.keySet()) {
            predicate = switch (key) {
                case "username" -> predicate.and(user -> user.getUsername().equals(queryParams.getFirst(key)));
                case "roles" -> predicate.and(user -> user.getRoles().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replace("ROLE_", ""))
                        .toList()
                        .containsAll(queryParams.get(key)));
                default -> predicate;
            };
        }

        return predicate;
    }
}
