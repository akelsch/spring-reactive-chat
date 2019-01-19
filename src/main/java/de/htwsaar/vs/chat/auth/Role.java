package de.htwsaar.vs.chat.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
