package de.htwsaar.vs.chat.event;

import de.htwsaar.vs.chat.auth.ChatAuthority;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DummyDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("nimda"));
        admin.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));
        admin.setStatus("At work");

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setStatus("At the gym");

        Chat chat = new Chat();
        chat.setName("Testchat");
        chat.setMembers(Set.of(admin, user));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setContent("Hello World");

        Mono<Void> deleteAll = userRepository.deleteAll()
                .then(chatRepository.deleteAll())
                .then(messageRepository.deleteAll());

        deleteAll
                .thenMany(userRepository.saveAll(List.of(admin, user)))
                .then(chatRepository.save(chat))
                .flatMap(c -> grantChatAuthorityToAdmin(c, admin))
                .then(messageRepository.save(message))
                .subscribe(m -> logCreatedUsers(admin, user));
    }

    private Mono<User> grantChatAuthorityToAdmin(Chat c, User admin) {
        admin.addAuthority(new ChatAuthority(c.getId()));
        return userRepository.save(admin);
    }

    private void logCreatedUsers(User admin, User user) {
        log.info("Created user: admin/nimda, roles={}, authorities={}", admin.getRoles(), admin.getAuthorities());
        log.info("Created user: user/123456, roles={}, authorities={}", user.getRoles(), user.getAuthorities());
        log.info("You can use them to login at /auth/signin");
    }
}
