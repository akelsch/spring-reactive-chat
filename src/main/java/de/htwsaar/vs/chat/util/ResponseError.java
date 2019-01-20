package de.htwsaar.vs.chat.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@UtilityClass
public class ResponseError {

    public static Mono<? extends ServerResponse> badRequest(Throwable e) {
        return Mono.error(new ResponseStatusException(BAD_REQUEST, e.getMessage(), e));
    }

    public static Mono<? extends ServerResponse> conflict(Throwable e) {
        return Mono.error(new ResponseStatusException(CONFLICT, e.getMessage(), e));
    }
}
