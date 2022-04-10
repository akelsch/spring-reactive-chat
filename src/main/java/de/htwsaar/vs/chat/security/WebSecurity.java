package de.htwsaar.vs.chat.security;

import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Class that contains Web Security Expressions used by Spring Security
 * (e.g. in annotations like {@link PreAuthorize} and {@link PostAuthorize}).
 *
 * @author Arthur Kelsch
 * @author Julian Quint
 * @author Mahan Karimi
 */
@Component
@RequiredArgsConstructor
public class WebSecurity {

    private final UserRepository userRepository;

    /* CHAT */
    public boolean addChatAuthority(Authentication authentication, String chatId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.user();

        user.addAuthority(new ChatAuthority(chatId));
        userRepository.save(user).subscribe();

        return true;
    }

    public boolean hasChatAuthority(Authentication authentication, String chatId) {
        return authentication.getAuthorities().contains(new ChatAuthority(chatId));
    }

    public boolean removeChatAuthority(Authentication authentication, String chatId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.user();

        if (user.removeAuthority(new ChatAuthority(chatId))) {
            userRepository.save(user).subscribe();
        }

        return true;
    }

    /* MESSAGE */
    public boolean isMessageSender(Authentication authentication, Message message) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.user();

        return user.getId().equals(message.getSender().getId());
    }
}
