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
import static org.springframework.http.HttpStatus.CONFLICT;
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
    void duplicateUsernameSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isEqualTo(CONFLICT);
    }

    @Test
    void invalidJsonSignup() {
        String payload = "{\"username\": woops}";

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void emptyUsernameSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void emptyPasswordSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void emptyPayloadSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void missingUsernameSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void missingPasswordSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void missingPayloadSignup() {
        Map<String, String> payload = new LinkedHashMap<>();

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void nullUsernameSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void nullPasswordSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void nullPayloadSignup() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
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

    @Test
    void invalidJsonSignin() {
        String payload = "{\"username\": woops}";

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void emptyUsernameSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void emptyPasswordSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void emptyPayloadSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void missingUsernameSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void missingPasswordSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void missingPayloadSignin() {
        Map<String, String> payload = new LinkedHashMap<>();

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void nullUsernameSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void nullPasswordSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void nullPayloadSignin() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
