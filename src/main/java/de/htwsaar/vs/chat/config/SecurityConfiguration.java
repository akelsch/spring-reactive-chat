package de.htwsaar.vs.chat.config;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationConverter;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationSuccessHandler;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthorizationConverter;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthorizationManager;
import de.htwsaar.vs.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
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
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import java.util.List;

/**
 * Configuration class for Spring Security.
 *
 * @author Arthur Kelsch
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserRepository userRepository;

    @Value("${chat.https.enabled:false}")
    private boolean httpsEnabled;

    @Value("${chat.cors.allowedOrigin:http://localhost:8080}")
    private String allowedOrigin;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .cors().and()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/signup").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthorizationFilter(), SecurityWebFiltersOrder.AUTHORIZATION);

        if (httpsEnabled) {
            http
                    .redirectToHttps().and()
                    .headers(headers -> headers
                            .hsts(hsts -> hsts.includeSubdomains(false)));
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(allowedOrigin);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);
        configuration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> userRepository
                .findByUsername(username)
                .map(UserPrincipal::new);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private WebFilter jwtAuthenticationFilter() {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder());

        var jwtAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        jwtAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        jwtAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/signin"));

        return jwtAuthenticationFilter;
    }

    private WebFilter jwtAuthorizationFilter() {
        var jwtAuthorizationFilter = new AuthenticationWebFilter(new JwtAuthorizationManager(userDetailsService()));
        jwtAuthorizationFilter.setServerAuthenticationConverter(new JwtAuthorizationConverter());
        jwtAuthorizationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());

        return jwtAuthorizationFilter;
    }
}
