package de.htwsaar.vs.chat.configuration;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration class for Spring Security.
 *
 * @author Arthur Kelsch
 */
@Configuration
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
                .pathMatchers("/api/**").authenticated()
                .anyExchange().permitAll()
                .and()
                .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
