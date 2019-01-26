package de.htwsaar.vs.chat.auth.jwt;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

public class JwtAuthorizationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthorizationManager(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String username = authentication.getName();

        return userDetailsService
                .findByUsername(username)
                .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException(username)))
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }
}
