package de.htwsaar.vs.chat.auth;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Class that contains Web Security Expressions used by Spring Security
 * (e.g. in annotations like {@link PreAuthorize} and {@link PostAuthorize}).
 *
 * @author Arthur Kelsch
 */
@Component
public class WebSecurity {

    private final UserRepository userRepository;

    @Autowired
    public WebSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addChatAuthority(Authentication authentication, Chat chat) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        user.addAuthority(new SimpleGrantedAuthority(String.format("CHAT_%s_ADMIN", chat.getId())));

        userRepository.save(user).subscribe();

        return true;
    }

    public boolean hasChatAuthority(Authentication authentication, String chatId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(String.format("CHAT_%s_ADMIN", chatId)));
    }
}
