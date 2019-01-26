package de.htwsaar.vs.chat.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static de.htwsaar.vs.chat.util.JwtUtil.createBearerToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        exchange
                .getResponse()
                .getHeaders()
                .add(AUTHORIZATION, createBearerToken(authentication));

        return webFilterExchange.getChain().filter(exchange);
    }
}
