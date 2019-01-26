package de.htwsaar.vs.chat.configuration;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.auth.jwt.JsonAuthenticationSuccessHandler;
import de.htwsaar.vs.chat.auth.jwt.JsonAuthenticationConverter;
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

    private static final String AUTH_SIGNUP_MATCHER = "/auth/signup";
    private static final String AUTH_SIGNIN_MATCHER = "/auth/signin";
    private static final String API_MATCHER = "/api/**";

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
                .pathMatchers(AUTH_SIGNUP_MATCHER).permitAll()
                .pathMatchers(AUTH_SIGNIN_MATCHER, API_MATCHER).authenticated()
                .anyExchange().permitAll()
                .and()
                .addFilterAt(jsonAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private WebFilter jsonAuthenticationFilter() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder());

        AuthenticationWebFilter jsonAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        jsonAuthenticationFilter.setServerAuthenticationConverter(new JsonAuthenticationConverter());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(new JsonAuthenticationSuccessHandler());
        jsonAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers(AUTH_SIGNIN_MATCHER));

        return jsonAuthenticationFilter;
    }

    private WebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter jwtAuthenticationFilter =
                new AuthenticationWebFilter(new JwtAuthenticationManager(userDetailsService()));
        jwtAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        jwtAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers(API_MATCHER));

        return jwtAuthenticationFilter;
    }
}
