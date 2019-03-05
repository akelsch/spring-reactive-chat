package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.Password;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Handler methods for {@link UserRouter}.
 *
 * @author Arthur Kelsch
 * @author Mahan Karimi
 * @author Leslie Marxen
 * @author Julian Quint
 */
@Component
public class UserHandler {

    private static final int ROLE_PREFIX_LENGTH = 5;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public UserHandler(UserService userService, PasswordEncoder passwordEncoder, Validator validator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
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
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> changePassword(ServerRequest request) {
        String uid = request.pathVariable("uid");
        Mono<Password> password = request
                .bodyToMono(Password.class)
                .doOnNext(this::validatePassword);

        return userService
                .findById(uid)
                .zipWith(password)
                .doOnNext(this::matchOldPassword)
                .doOnNext(tuple -> tuple.getT1().setPassword(tuple.getT2().getNewPassword()))
                .flatMap(tuple -> userService.update(tuple.getT1()))
                .flatMap(user -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private static Predicate<User> matchByQueryParams(MultiValueMap<String, String> queryParams) {
        Predicate<User> predicate = user -> true;

        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            switch (key) {
                case "username":
                    predicate = predicate.and(user -> user.getUsername().equals(values.get(0)));
                    break;
                case "roles":
                    predicate = predicate.and(user -> {
                        List<String> roles = user.getRoles().stream()
                                .map(GrantedAuthority::getAuthority)
                                .map(role -> role.substring(ROLE_PREFIX_LENGTH))
                                .collect(Collectors.toList());
                        return roles.containsAll(values);
                    });
                    break;
            }
        }

        return predicate;
    }

    private void validatePassword(Password password) {
        Set<ConstraintViolation<Password>> violations = validator.validate(password);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void matchOldPassword(Tuple2<User, Password> tuple) {
        String givenPassword = tuple.getT2().getOldPassword();
        String actualEncodedPassword = tuple.getT1().getPassword();

        if (!passwordEncoder.matches(givenPassword, actualEncodedPassword)) {
            throw new ServerWebInputException("Old password does not match");
        }
    }
}
