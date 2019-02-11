package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link UserService}.
 *
 * @author Arthur Kelsch
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void save() {
        given(passwordEncoder.encode(any())).willReturn("encoded");
        given(userRepository.save(any())).willAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            return Mono.just(arg.withId("42")); // Clone the user and give him an ID
        });

        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .build();

        User expected = User.builder()
                .id("42")
                .username("testuser")
                .password("encoded")
                .roles(Collections.singletonList(Role.USER))
                .build();

        StepVerifier.create(userService.save(user))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void update() {
        given(passwordEncoder.encode(any())).willReturn("encoded");
        given(userRepository.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        User user = User.builder()
                .id("42")
                .username("testuser")
                .password("testpassword")
                .roles(Arrays.asList(Role.USER, Role.ADMIN))
                .build();

        User expected = User.builder()
                .id("42")
                .username("testuser")
                .password("encoded")
                .roles(Arrays.asList(Role.USER, Role.ADMIN))
                .build();

        StepVerifier.create(userService.update(user))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void findById() {
        given(userRepository.findById("42")).willReturn(Mono.just(User.builder().username("testuser").build()));

        StepVerifier.create(userService.findById("42"))
                .expectNextMatches(u -> u.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void findAll() {
        given(userRepository.findAll()).willReturn(Flux.just(User.builder().username("testuser1").build(),
                User.builder().username("testuser2").build()));

        StepVerifier.create(userService.findAll())
                .expectNextMatches(u -> u.getUsername().equals("testuser1"))
                .expectNextMatches(u -> u.getUsername().equals("testuser2"))
                .verifyComplete();
    }

    @Test
    void deleteById() {
        given(userRepository.deleteById("42")).willReturn(Mono.empty());

        StepVerifier.create(userService.deleteById("42"))
                .verifyComplete();
    }
}
