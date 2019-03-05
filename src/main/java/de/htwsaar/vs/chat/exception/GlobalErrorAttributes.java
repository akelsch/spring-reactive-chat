package de.htwsaar.vs.chat.exception;

import java.util.Map;
import java.util.Optional;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;

import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 *
 * @author Julian Quint
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
        Optional<HttpStatus> errorStatus = determineHttpStatus(getError(request));
        errorStatus.ifPresent(httpStatus -> {
            map.replace("status", httpStatus.value());
            map.replace("error", httpStatus.getReasonPhrase());
        });
        return map;
    }

    private Optional<HttpStatus> determineHttpStatus(Throwable error) {
        if (error instanceof DecodingException || error instanceof ConstraintViolationException) {
            return Optional.of(BAD_REQUEST);
        }
        if (error instanceof DuplicateKeyException) {
            return Optional.of(CONFLICT);
        }
        return Optional.empty();
    }

}


