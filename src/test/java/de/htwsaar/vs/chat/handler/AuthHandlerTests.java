package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.DisableWebFluxSecurityCsrf;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.AuthRouter;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Unit tests for {@link AuthHandler}.
 *
 * @author Arthur Kelsch
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest
@DisableWebFluxSecurityCsrf
@Import({AuthRouter.class, AuthHandler.class})
class AuthHandlerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void signup() {
        User user = new User();
        user.setId("42");
        given(userService.save(any())).willReturn(Mono.just(user));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("username", "testuser");
        payload.put("password", "testpassword");

        webTestClient
                .post().uri("/auth/signup")
                .contentType(APPLICATION_JSON)
                .syncBody(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals(LOCATION, "/api/v1/users/42");
    }

    @Test
    void signin() {
        // Handler itself does nothing except returning 200
        webTestClient
                .post().uri("/auth/signin")
                .exchange()
                .expectStatus().isOk();
    }
}
