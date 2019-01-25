package de.htwsaar.vs.chat.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import de.htwsaar.vs.chat.util.ResponseError;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static de.htwsaar.vs.chat.util.JwtUtil.JWT_PREFIX;
import static de.htwsaar.vs.chat.util.JwtUtil.JWT_SECRET;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
                .filter(authorization -> authorization.toLowerCase().startsWith(JWT_PREFIX.toLowerCase()))
                .map(authorization -> authorization.substring(JWT_PREFIX.length()))
                .map(JwtAuthenticationConverter::verifyToken)
                .onErrorResume(JWTVerificationException.class,
                        e -> ResponseError.badRequest(e, "Could not verify JWT token"))
                .map(username -> new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
    }

    private static String verifyToken(String token) {
        return JWT.require(HMAC256(JWT_SECRET.getBytes()))
                .build()
                .verify(token)
                .getClaim("name")
                .asString();
    }
}
