package de.htwsaar.vs.chat.configuration;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationConverter;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthenticationSuccessHandler;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthorizationConverter;
import de.htwsaar.vs.chat.auth.jwt.JwtAuthorizationManager;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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

    @Value("${chat.https.enabled:false}")
    private boolean httpsEnabled;

    @Value("${chat.cors.allowedOrigin:http://localhost:8080}")
    private String allowedOrigin;

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
                .cors()
                .and()
                .authorizeExchange()
                .pathMatchers(AUTH_SIGNUP_MATCHER).permitAll()
                .pathMatchers(AUTH_SIGNIN_MATCHER, API_MATCHER).authenticated()
                .anyExchange().permitAll()
                .and()
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthorizationFilter(), SecurityWebFiltersOrder.AUTHORIZATION);

        if (httpsEnabled) {
            http
                    .redirectToHttps()
                    .and()
                    .headers()
                    .hsts().includeSubdomains(false);
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(allowedOrigin);
        configuration.setAllowedMethods(asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addExposedHeader(AUTHORIZATION);
        configuration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private WebFilter jwtAuthenticationFilter() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder());

        AuthenticationWebFilter jwtAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        jwtAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        jwtAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers(AUTH_SIGNIN_MATCHER));

        return jwtAuthenticationFilter;
    }

    private WebFilter jwtAuthorizationFilter() {
        AuthenticationWebFilter jwtAuthorizationFilter =
                new AuthenticationWebFilter(new JwtAuthorizationManager(userDetailsService()));
        jwtAuthorizationFilter.setServerAuthenticationConverter(new JwtAuthorizationConverter());
        jwtAuthorizationFilter.setRequiresAuthenticationMatcher(pathMatchers(API_MATCHER));

        return jwtAuthorizationFilter;
    }
}
