package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import de.htwsaar.vs.chat.router.AuthRouter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author Leslie Marxen
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class UserIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        users.clear();

        users.addAll(userRepository.findAll().collectList().block());
        users.forEach(x -> x.setPassword(null));
        for (int i = 0; i < 20; i++) {
            User u = new User();

            u.setUsername(String.format("t%d", i));
            u.setPassword(String.format("p%d", i));

            switch (i % 2) {
                case 0:
                    u.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
                    break;
                case 1:
                    u.setRoles(Arrays.asList(Role.USER));
                    break;
            }


            userRepository.save(u).subscribe();
            u.setPassword(null);
            users.add(u);


        }
    }


    @Test
    @WithMockUser
    void getAll() {

        webTestClient
                .get().uri("/api/v1/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(
                        usrs -> assertThat(usrs.getResponseBody().size() == users.size())
                );

    }

    @Test
    @WithMockUser
    void getAdmins() {

        webTestClient
                .get().uri("/api/v1/users?roles=ADMIN")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(usrs -> assertThat(usrs.getResponseBody())
                        .extracting("roles").contains(Role.ADMIN, Role.USER));
    }

    @Test
    @WithMockUser
    void getUsers() {

        final List<User> notAdmins = users.stream()
                .filter(x -> x.getRoles().contains(Role.USER))
                .collect(Collectors.toList());

        webTestClient
                .get().uri("/api/v1/users?group=USER")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(usrs -> assertThat(users.containsAll(notAdmins)));
    }

    @Test
    @WithMockUser
    void getSingleUser() {
        final int rand = new Random().nextInt(users.size());
        final User u = userRepository.findByUsername(users.get(rand).getUsername()).block();

        webTestClient
                .get().uri("/api/v1/users/" + u.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(usrs -> assertThat(users.size() == 1 && users.contains(u)));


    }

    @Test
    @WithMockUser
    void deleteUser() {

        final User u = userRepository.findByUsername(users.get(0).getUsername()).block();
        final String id = u.getId();

        webTestClient
                .delete().uri("/api/v1/users/" + id)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get().uri("/api/v1/users/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void changePassword() {
        final User u = userRepository.findByUsername(users.get(0).getUsername()).block();

        webTestClient
                .post().uri(String.format("/api/v1/users/%s/changePassword", u.getId()))
                .exchange()
                .expectStatus().isOk();

    }


    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getNonExistingUser() {
        final String id = "aaa";

        webTestClient
                .get().uri(String.format("/api/v1/users/%s", id))
                .exchange()
                .expectStatus().isNotFound();
    }


}























