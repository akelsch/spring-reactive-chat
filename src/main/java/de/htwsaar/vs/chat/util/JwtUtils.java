package de.htwsaar.vs.chat.util;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import de.htwsaar.vs.chat.auth.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;


/**
 * Utility class providing methods to work with JSON Web Tokens.
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public final class JwtUtils {

    private static final long EXPIRES_IN = 24 * 60 * 60 * 1000L;

    private static final byte[] SECRET = "avtQCXgvuLGn93dB3Mm8UXL9yLNqUXDM".getBytes();
    private static final SecretKey SECRET_KEY = new SecretKeySpec(SECRET, "HmacSHA256");

    private static final JwtEncoder jwtEncoder = createJwtEncoder();

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

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(userPrincipal.getId())
                .claim("name", userPrincipal.getUsername())
                .expiresAt(Instant.now().plusMillis(EXPIRES_IN))
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet));

        return jwt.getTokenValue();
    }

    public static JwtEncoder createJwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(SECRET_KEY));
    }

    public static ReactiveJwtDecoder createJwtDecoder() {
        return NimbusReactiveJwtDecoder.withSecretKey(SECRET_KEY).build();
    }
}
