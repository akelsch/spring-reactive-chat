package de.htwsaar.vs.chat.auth.jwt;

import de.htwsaar.vs.chat.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Adds an Authorization HTTP header with a token to the response.
 *
 * @author Arthur Kelsch
 */
public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        String token = JwtUtils.createToken(authentication);
        exchange.getResponse().getHeaders().setBearerAuth(token);

        return webFilterExchange.getChain().filter(exchange);
    }
}
