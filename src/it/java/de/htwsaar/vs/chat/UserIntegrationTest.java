package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import de.htwsaar.vs.chat.router.AuthRouter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class UserIntegrationTest  {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    private final User users[] = new User[20];

    @BeforeEach
    void setUp(){
        final StringBuilder jsonBuilder = new StringBuilder();
        for(int i = 0; i < users.length; i++){
            User u = new User();

            u.setUsername(String.format("t%d", i));
            u.setPassword(String.format("p%d", i));

            switch(i % 2){
                case 0:
                    u.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
                    break;
                case 1:
                    u.setRoles(Arrays.asList(Role.USER));
                    break;
            }

            userRepository.save(u).subscribe();
            users[i] = u;


        }
    }

    @AfterEach
    void clearUp(){
        userRepository.deleteAll();
    }


    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getAll(){

        webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile("(.*\"id\".*){" + users.length + "}")::matcher);
        // contains users.length users
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getAdmins(){
        webTestClient
                .get().uri("/api/v1/users?group=ADMIN")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile("(.*\"id\".*){" + (users.length / 2) + "}")::matcher);
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getUsers(){
        webTestClient
                .get().uri("/api/v1/users?group=USER")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile("(.*\"id\".*){" + users.length + "}")::matcher);
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getSingleUser(){
        final User u = userRepository.findByUsername(users[0].getUsername()).block();

        webTestClient
                .get().uri("/api/v1/users/" + u.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile(String.format("(.*%s.*)", u.getId()))::matcher);

    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void deleteUser(){
        final User u = userRepository.findByUsername(users[0].getUsername()).block();
        final String id = u.getId();

        webTestClient
                .delete().uri("/api/v1/users/" + id)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get().uri("/api/v1/users/"  + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void changePassword(){
        final User u = userRepository.findByUsername(users[0].getUsername()).block();

        webTestClient
                .post().uri(String.format("/api/v1/users/%s/changePassword", u.getId()))
                .exchange()
                .expectStatus().isOk();

    }


    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getNonExistingUser(){
        final String id = "aaa";

        webTestClient
                .get().uri(String.format("/api/v1/users/%s", id))
                .exchange()
                .expectStatus().isNotFound();
    }






}























