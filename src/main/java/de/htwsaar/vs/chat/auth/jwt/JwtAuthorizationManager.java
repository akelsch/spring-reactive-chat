package de.htwsaar.vs.chat.auth.jwt;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

/**
 * Finds an authenticated user in the database and authorizes him.
 *
 * @author Arthur Kelsch
 */
public class JwtAuthorizationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthorizationManager(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();

        return userDetailsService
                .findByUsername(username)
                .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException(username)))
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }
}
