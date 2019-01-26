package de.htwsaar.vs.chat.util;

import com.auth0.jwt.JWT;
import de.htwsaar.vs.chat.auth.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

/**
 * Utility class providing methods to work with JSON Web Tokens.
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public class JwtUtil {

    public static final String JWT_SECRET = "avtQCXgvuLGn93dB3Mm8UXL9yLNqUXDM";
    public static final String JWT_PREFIX = "Bearer ";

    private static final long JWT_EXP = 86_400_000;

    public static String createBearerToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = JWT.create()
                .withSubject(userPrincipal.getId())
                .withClaim("name", userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXP))
                .sign(HMAC256(JWT_SECRET.getBytes()));

        return JWT_PREFIX + token;
    }
}
