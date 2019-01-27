package de.htwsaar.vs.chat.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Utility class wrapping exceptions so they dont cause 500 Internal Server Errors.
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public class ResponseError {

    public static <T> Mono<T> badRequest(Throwable e) {
        return badRequest(e, e.getMessage());
    }

    public static <T> Mono<T> badRequest(Throwable e, String message) {
        return Mono.error(() -> new ResponseStatusException(BAD_REQUEST, message, e));
    }

    public static <T> Mono<T> conflict(Throwable e) {
        return Mono.error(() -> new ResponseStatusException(CONFLICT, e.getMessage(), e));
    }
}
