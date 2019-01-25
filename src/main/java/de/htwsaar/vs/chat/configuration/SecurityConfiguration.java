package de.htwsaar.vs.chat.configuration;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.auth.jwt.BasicAuthenticationSuccessHandler;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationConverter;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationManager;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.WebFilter;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

/**
 * Configuration class for Spring Security.
 *
 * @author Arthur Kelsch
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final UserRepository userRepository;

    @Autowired
    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> userRepository
                .findByUsername(username)
                .map(UserPrincipal::new);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/auth/signup").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(basicAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private WebFilter basicAuthenticationFilter() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder());

        AuthenticationWebFilter basicAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        basicAuthenticationFilter.setAuthenticationSuccessHandler(new BasicAuthenticationSuccessHandler());
        basicAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers("/auth/signin"));

        return basicAuthenticationFilter;
    }

    private WebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter jwtAuthenticationFilter = new AuthenticationWebFilter(new JwtAuthenticationManager());
        jwtAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        jwtAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers("/api/**"));

        return jwtAuthenticationFilter;
    }
}
