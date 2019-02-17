package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.User;
import lombok.Getter;
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

    @Getter
    private final String authority;
}
