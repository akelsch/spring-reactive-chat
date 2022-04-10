package de.htwsaar.vs.chat.security.authentication;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class SigninAuthenticationWebFilter extends AuthenticationWebFilter {

    private static final String SIGNIN_PATH = "/auth/signin";

    public SigninAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(SIGNIN_PATH));
        this.setServerAuthenticationConverter(new SigninServerAuthenticationConverter());
        this.setAuthenticationSuccessHandler(new SigninServerAuthenticationSuccessHandler());
    }
}
