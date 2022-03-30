package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.router.AuthRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Integration tests for routes defined in {@link AuthRouter}.
 *
 * @author Arthur Kelsch
 */
@SpringBootTest
@AutoConfigureWebTestClient
class AuthIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void signupWithValidPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches(LOCATION, "/api/v1/users/\\w{24}");
    }

    @Test
    void signupWithMalformedPayload() {
        String payload = "{\"username\": woops}";

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithDuplicateUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(CONFLICT);
    }

    @Test
    void signupWithEmptyUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithEmptyPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithEmptyPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithMissingUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithMissingPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithMissingPayload() {
        Map<String, String> payload = new LinkedHashMap<>();

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithNullUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithNullPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser2");
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signupWithNullPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signinWithValidPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches(AUTHORIZATION, "Bearer .+\\..+\\..+");
    }

    @Test
    void signinWithMalformedPayload() {
        String payload = "{\"username\": woops}";

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signinWithEmptyUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithEmptyPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithEmptyPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "");
        payload.put("password", "");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithMissingUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithMissingPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithMissingPayload() {
        Map<String, String> payload = new LinkedHashMap<>();

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithNullUsername() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", "nimda");

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithNullPassword() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "admin");
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signinWithNullPayload() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", null);
        payload.put("password", null);

        webTestClient
                .post().uri("/auth/signin")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
