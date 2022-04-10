package de.htwsaar.vs.chat.config.authentication;

import de.htwsaar.vs.chat.util.JwtUtils;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Adds an Authorization HTTP header containing a Bearer token to the response
 * once {@link UserDetailsRepositoryReactiveAuthenticationManager} was able to
 * successfully authenticate the user in the database.
 *
 * @author Arthur Kelsch
 */
class SigninServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        String token = JwtUtils.createToken(authentication);
        exchange.getResponse().getHeaders().setBearerAuth(token);

        return webFilterExchange.getChain().filter(exchange);
    }
}
