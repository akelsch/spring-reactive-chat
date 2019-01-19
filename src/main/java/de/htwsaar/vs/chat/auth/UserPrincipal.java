package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * {@link UserDetails} implementation used by Spring Security for authentication
 * and authorization.
 *
 * @author Arthur Kelsch
 * @see User
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableSet(new TreeSet<>(user.getRoles()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
