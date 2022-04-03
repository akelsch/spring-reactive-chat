package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.UserRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Integration tests for routes defined in {@link UserRouter}.
 *
 * @author Leslie Marxen
 */
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
                .consumeWith(response -> assertThat(response.getResponseBody().getId())
                        .isEqualTo(user.getId()));
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

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void delete() {
        User user = userService.findAll()
                .filter(u -> !u.getUsername().equals("admin"))
                .blockFirst();

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
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("oldPassword", "testpassword");
        payload.put("newPassword", "newpassword123");

        webTestClient
                .post().uri("/api/v1/users/{id}/change_password", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithValidPayload() {
        User user = new User();
        user.setUsername("testuser2");
        user.setPassword("testpassword");
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "ROLE_TEST");

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();

        assertThat(userService.findById(user.getId()).block().getRoles())
                .contains(new SimpleGrantedAuthority("ROLE_TEST"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithEmptyRole() {
        User user = new User();
        user.setUsername("testuser3");
        user.setPassword("testpassword");
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "");

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void putRoleWithInvalidRole() {
        User user = new User();
        user.setUsername("testuser4");
        user.setPassword("testpassword");
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "_TEST");

        webTestClient
                .put().uri("/api/v1/users/{id}/roles", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(userService.findById(user.getId()).block().getRoles())
                .doesNotContain(new SimpleGrantedAuthority("_TEST"));
    }

    @Disabled("WebTestClient does not support DELETE request bodies")
    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void deleteRole() {
        User user = new User();
        user.setUsername("testuser5");
        user.setPassword("testpassword");
        user.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("role", "ROLE_ADMIN");

        webTestClient
                .delete().uri("/api/v1/users/{id}/roles", user.getId())
                /*.contentType(APPLICATION_JSON)
                .bodyValue(payload)*/
                .exchange()
                .expectStatus().isNoContent();

        assertThat(userService.findById(user.getId()).block().getRoles())
                .doesNotContain(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Disabled("Does not work due to dynamic property 'id' of UserPrincipal")
    @Test
    @WithMockUser
    void putStatusWithValidPayload() {
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("testpassword");
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("status", "Hey there! I am using Spring WebFlux");

        webTestClient
                .put().uri("/api/v1/users/{id}/status", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();

        assertThat(userService.findById(user.getId()).block().getStatus())
                .isEqualTo("Hey there! I am using Spring WebFlux");
    }

    @Disabled("Does not work due to dynamic property 'id' of UserPrincipal")
    @Test
    @WithMockUser
    void putStatusWithEmptyStatus() {
        User user = new User();
        user.setUsername("testuser7");
        user.setPassword("testpassword");
        user = userService.createUser(user).block();

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("status", "");

        webTestClient
                .put().uri("/api/v1/users/{id}/status", user.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Disabled("Does not work due to dynamic property 'id' of UserPrincipal")
    @Test
    @WithMockUser
    void deleteStatus() {
        User user = new User();
        user.setUsername("testuser8");
        user.setPassword("testpassword");
        user.setStatus("Hey there! I am using Spring WebFlux");
        user = userService.createUser(user).block();

        webTestClient
                .delete().uri("/api/v1/users/{id}/status", user.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(userService.findById(user.getId()).block().getStatus()).isEmpty();
    }
}
