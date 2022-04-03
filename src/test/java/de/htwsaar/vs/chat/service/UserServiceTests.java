package de.htwsaar.vs.chat.service;

import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.model.user.PasswordRequest;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link UserService}.
 *
 * @author Arthur Kelsch
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        given(passwordEncoder.encode(any())).willReturn("encoded");
        given(userRepository.save(any())).willAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            // Clone the user and give him an ID
            User user = new User();
            user.setUsername(arg.getUsername());
            user.setPassword(arg.getPassword());
            user.setId("42");
            return Mono.just(user);
        });

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        User expected = new User();
        expected.setId("42");
        expected.setUsername("testuser");
        expected.setPassword("encoded");

        StepVerifier.create(userService.createUser(user))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void updatePassword() {
        given(passwordEncoder.matches("testpassword", "testpassword")).willReturn(true);
        given(passwordEncoder.encode(any())).willReturn("encoded");
        given(userRepository.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        User user = new User();
        user.setId("42");
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setOldPassword("testpassword");
        passwordRequest.setNewPassword("newpassword");

        User expected = new User();
        expected.setId("42");
        expected.setUsername("testuser");
        expected.setPassword("encoded");
        expected.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        StepVerifier.create(userService.updatePassword(user, passwordRequest))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void updateRoles() {
        given(userRepository.save(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        User user = new User();
        user.setId("42");
        user.setUsername("testuser");
        user.setPassword("encoded");
        user.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        User expected = new User();
        expected.setId("42");
        expected.setUsername("testuser");
        expected.setPassword("encoded");
        expected.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        StepVerifier.create(userService.updateRoles(user))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void findById() {
        User user = new User();
        user.setUsername("testuser");
        given(userRepository.findById("42")).willReturn(Mono.just(user));

        StepVerifier.create(userService.findById("42"))
                .expectNextMatches(u -> u.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void findAll() {
        User user1 = new User();
        user1.setUsername("testuser1");
        User user2 = new User();
        user2.setUsername("testuser2");
        given(userRepository.findAll()).willReturn(Flux.just(user1, user2));

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
