package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.router.AuthRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Integration test testing routes defined in {@link AuthRouter}.
 *
 * @author Arthur Kelsch
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class AuthIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void successfulSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches(LOCATION, "\\/api\\/v1\\/users\\/\\w{24}");
    }

    @Test
    void successfulSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches(AUTHORIZATION, "Bearer .+\\..+\\..+");
    }
}
