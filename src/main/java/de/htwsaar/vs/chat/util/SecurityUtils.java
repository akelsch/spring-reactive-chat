package de.htwsaar.vs.chat.util;

import de.htwsaar.vs.chat.auth.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * Utility class providing methods to with Spring Security (authentication etc).
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Gets a {@link UserPrincipal} object (a custom {@link UserDetails}
     * implementation) from the current Spring Security authentication.
     * <p>
     * Important: Spring has to subscribe to the Mono with a context or otherwise
     * the result will be empty.
     *
     * @return a {@link Mono} of said {@link UserPrincipal}
     */
    public static Mono<UserPrincipal> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserPrincipal.class);
    }
}
