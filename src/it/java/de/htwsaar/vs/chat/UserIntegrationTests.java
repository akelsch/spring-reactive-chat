package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Integration tests for routes defined in {@link UserRouter}.
 *
 * @author Leslie Marxen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Test
    @WithMockUser
    void getAllUsers() {
        webTestClient
                .get().uri("/api/v1/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class);
    }

    @Test
    @WithMockUser
    void getAllUsersWithFilter() {
        webTestClient
                .get().uri("/api/v1/users?roles=ADMIN")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(response -> assertThat(response.getResponseBody())
                        .extracting("username").containsOnly("admin"));
    }

    @Test
    @WithMockUser
    void getExistingUser() {
        User user = userService.findAll().blockFirst();

        webTestClient
                .get().uri("/api/v1/users/{id}", user.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(user));
    }

    @Test
    @WithMockUser
    void getNonExistingUser() {
        webTestClient
                .get().uri("/api/v1/users/{id}", "woops")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Disabled("Does not work due to dynamic property 'id' of UserPrincipal")
    @Test
    @WithMockUser
    void delete() {
        User user = userService.findAll().blockFirst();

        webTestClient
                .delete().uri("/api/v1/users/{id}", user.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Disabled("Does not work due to dynamic property 'id' of UserPrincipal")
    @Test
    @WithMockUser
    void changePassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user = userService.save(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("oldPassword", "testpassword");
        payload.put("newPassword", "newpassword123");

        webTestClient
                .post().uri("/api/v1/users/{id}/changePassword", user.getId())
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(payload))
                .exchange()
                .expectStatus().isOk();
    }
}
