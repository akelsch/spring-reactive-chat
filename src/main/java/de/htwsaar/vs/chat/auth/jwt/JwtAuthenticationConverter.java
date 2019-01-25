package de.htwsaar.vs.chat.auth.jwt;

import com.auth0.jwt.JWT;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
        ServerHttpRequest request = exchange.getRequest();

        String authorization = request.getHeaders().getFirst(AUTHORIZATION);
        if (authorization == null || !authorization.toLowerCase().startsWith("bearer ")) {
            return Mono.empty();
        }

        String token = authorization.length() <= JWT_PREFIX.length() ?
                "" : authorization.substring(JWT_PREFIX.length());

        // TODO exception handling
        String username = JWT.require(HMAC256(JWT_SECRET.getBytes()))
                .build()
                .verify(token)
                .getClaim("name")
                .asString();

        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
    }
}
