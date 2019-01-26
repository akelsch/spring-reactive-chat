package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link GrantedAuthority} implementation used by Spring Security for authorization
 * purposes.
 *
 * @author Arthur Kelsch
 * @see User
 */
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
