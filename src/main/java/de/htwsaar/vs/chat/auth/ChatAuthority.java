package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.Chat;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * {@link GrantedAuthority} implementation similar to {@link SimpleGrantedAuthority}.
 * <p>
 * The authority string has the pattern {@code CHAT_%s_MOD} where {@code %s}
 * should be a {@link Chat} id.
 * <p>
 * Important: A no-args constructor is required so authorities fetched from the
 * database do not get wrapped by the pattern string again!
 *
 * @author Arthur Kelsch
 */
@NoArgsConstructor
@EqualsAndHashCode
public class ChatAuthority implements GrantedAuthority {

    private String authority;

    public ChatAuthority(String chatId) {
        this.authority = "CHAT_%s_MOD".formatted(chatId);
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
