package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthHandler}.
 *
 * @author Arthur Kelsch
 */
@ExtendWith(MockitoExtension.class)
class AuthHandlerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthHandler authHandler;

    @Test
    void signup() {
        User user = new User();
        user.setId("42");
        when(userService.createUser(any())).thenReturn(Mono.just(user));

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(new User()));
        ServerResponse response = authHandler.signup(request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.statusCode());
        assertEquals(URI.create("/api/v1/users/42"), response.headers().getLocation());
    }

    @Test
    void signin() {
        MockServerRequest request = MockServerRequest.builder().build();
        ServerResponse response = authHandler.signin(request).block();

        // The handler itself does nothing except returning 200
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }
}
