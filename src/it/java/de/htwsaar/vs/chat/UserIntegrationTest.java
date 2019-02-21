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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        users.clear();

        users.addAll(userRepository.findAll().collectList().block());
        users.stream().forEach(x -> x.setPassword(null));
        for(int i = 0; i < 20; i++){
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
            u.setPassword(null);
            users.add(u);


        }
    }


    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getAll(){

        final List<User> tmp = Arrays.asList(webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class).returnResult().getResponseBody());


        Assertions.assertTrue(tmp.containsAll(users));
    }
    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getAdmins(){


        final List<User> tmp = Arrays.asList(webTestClient
                .get().uri("/api/v1/users?roles=ADMIN")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class).returnResult().getResponseBody());

        Assertions.assertTrue(tmp.containsAll(users.stream()
                .filter(x -> x.getRoles().contains(Role.ADMIN))
                .collect(Collectors.toList())
        ));
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getUsers(){
        final List<User> tmp = Arrays.asList(webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class).returnResult().getResponseBody());

        Assertions.assertTrue(tmp.containsAll(users.stream()
                .filter(x -> x.getRoles().contains(Role.USER))
                .collect(Collectors.toList())
        ));
    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void getSingleUser(){
        final int rand = new Random().nextInt(users.size());
        final User u = userRepository.findByUsername(users.get(rand).getUsername()).block();

        final List<User> tmp = Arrays.asList(webTestClient
                .get().uri("/api/v1/users/" + u.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(User[].class).returnResult().getResponseBody());

        Assertions.assertEquals(1, tmp.size());
        Assertions.assertTrue(tmp.containsAll(users));

    }

    @Test
    @WithMockUser(username = "t0", password = "p0")
    void deleteUser(){

        final User u = userRepository.findByUsername(users.get(0).getUsername()).block();
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
        final User u = userRepository.findByUsername(users.get(0).getUsername()).block();

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























