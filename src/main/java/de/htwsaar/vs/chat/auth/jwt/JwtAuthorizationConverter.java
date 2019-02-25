package de.htwsaar.vs.chat.auth.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import de.htwsaar.vs.chat.util.JwtUtils;
import de.htwsaar.vs.chat.util.ResponseError;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static de.htwsaar.vs.chat.util.JwtUtils.JWT_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Converts a token from an Authorization HTTP header into an {@link Authentication}
 * object. This includes verifying the tokens signature.
 *
 * @author Arthur Kelsch
 */
public class JwtAuthorizationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
                .filter(authorization -> authorization.toLowerCase().startsWith(JWT_PREFIX.toLowerCase()))
                .map(authorization -> authorization.substring(JWT_PREFIX.length()))
                .map(JwtUtils::verifyToken)
                .map(JwtUtils::getName)
                .onErrorResume(JWTVerificationException.class,
                        e -> ResponseError.badRequest(e, "JWT verification failed: " + e.getMessage()))
                .map(username -> new UsernamePasswordAuthenticationToken(username, null));
    }
}
