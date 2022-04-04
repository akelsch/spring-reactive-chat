package de.htwsaar.vs.chat.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.htwsaar.vs.chat.auth.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Utility class providing methods to work with JSON Web Tokens.
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public final class JwtUtils {

    private static final long EXPIRES_IN = 24 * 60 * 60 * 1000L;

    private static final String SECRET = "avtQCXgvuLGn93dB3Mm8UXL9yLNqUXDM";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    /**
     * Creates a new JWT with the following claims:
     * <ol>
     * <li>sub = user id</li>
     * <li>name = user name</li>
     * <li>exp = 24h from now</li>
     * </ol>
     * <p>
     * The JWT is signed using HMAC-SHA256 (HS256).
     *
     * @param authentication the currently authenticated user
     * @return a JWT with the above described payload
     */
    public static String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return JWT.create()
                .withSubject(userPrincipal.getId())
                .withClaim("name", userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES_IN))
                .sign(ALGORITHM);
    }

    public static String unwrapBearerToken(String bearerToken) {
        String bearerPrefix = "bearer ";
        if (bearerToken.toLowerCase().startsWith(bearerPrefix)) {
            return bearerToken.substring(bearerPrefix.length());
        }

        return null;
    }

    /**
     * Verifies the signature of a given JWT.
     *
     * @param token the JWT to verify
     * @return a verified and decoded JWT
     */
    public static DecodedJWT verifyToken(String token) {
        return JWT.require(ALGORITHM)
                .withClaimPresence("name")
                .build()
                .verify(token);
    }

    /**
     * Gets the claim called "name" from a given decoded JWT.
     *
     * @param jwt the decoded JWT
     * @return the "name" claim
     */
    public static String getName(DecodedJWT jwt) {
        return jwt.getClaim("name").asString();
    }
}
