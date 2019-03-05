package de.htwsaar.vs.chat.exception;

import java.util.Map;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;

import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DuplicateKeyException;
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
        Throwable error = getError(request);
        if (error instanceof DecodingException || error instanceof ConstraintViolationException) {
            map.put("status", BAD_REQUEST.value());
            map.put("error", BAD_REQUEST.getReasonPhrase());
        } else if (error instanceof DuplicateKeyException) {
            map.put("status", CONFLICT.value());
            map.put("error", CONFLICT.getReasonPhrase());
        }
        return map;
    }

}


