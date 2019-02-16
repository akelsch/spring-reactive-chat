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
    private String usersJSON;

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
    @WithMockUser(username = "testuser", password = "testpassword")
    void getAll(){

        webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile("(.*\"id\".*){" + users.length + "}")::matcher);
        // contains users.length users
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    void getAdmins(){
        webTestClient
                .get().uri("/api/v1/users?group=ADMIN")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(Pattern.compile("(.*\"id\".*){" + (users.length / 2) + "}")::matcher);
    }



}























