package de.htwsaar.vs.chat.auth.jwt;

import de.htwsaar.vs.chat.util.JwtUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static de.htwsaar.vs.chat.util.JwtUtils.JWT_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

/**
 * Converts a token from an Authorization HTTP header into an {@link Authentication}
 * object. This includes verifying the tokens' signature.
 * <p>
 * If the Authorization header is empty, query parameters get checked for a
 * token as server-sent events do not support setting custom HTTP headers.
 * This requires the {@code text/event-stream} Accept header to be set.
 *
 * @author Arthur Kelsch
 */
public class JwtAuthorizationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        Mono<String> sseToken = Mono.justOrEmpty(request.getQueryParams().getFirst("token"))
                .filter(token -> request.getHeaders().getAccept().contains(TEXT_EVENT_STREAM));

        return Mono.justOrEmpty(request.getHeaders().getFirst(AUTHORIZATION))
                .switchIfEmpty(sseToken)
                .filter(authorization -> authorization.toLowerCase().startsWith(JWT_PREFIX.toLowerCase()))
                .map(authorization -> authorization.substring(JWT_PREFIX.length()))
                .map(JwtUtils::verifyToken)
                .map(JwtUtils::getName)
                .map(username -> new UsernamePasswordAuthenticationToken(username, null));
    }
}
