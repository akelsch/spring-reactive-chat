package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.Password;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import de.htwsaar.vs.chat.util.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.DecodingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

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
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public UserHandler(UserService userService, PasswordEncoder passwordEncoder, Validator validator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
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
        Mono<Password> password = request
                .bodyToMono(Password.class)
                .doOnNext(this::validatePassword)
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest);

        return userService
                .findById(uid)
                .zipWith(password)
                .doOnNext(this::matchOldPassword)
                .doOnNext(t -> t.getT1().setPassword(t.getT2().getNewPassword()))
                .flatMap(t -> userService.update(t.getT1()))
                .flatMap(u -> ServerResponse.ok().build())
                .switchIfEmpty(ServerResponse.notFound().build());
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
