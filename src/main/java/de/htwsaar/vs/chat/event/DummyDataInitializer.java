package de.htwsaar.vs.chat.event;

import de.htwsaar.vs.chat.auth.ChatAuthority;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@Profile("dev")
public class DummyDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DummyDataInitializer(UserRepository userRepository, ChatRepository chatRepository,
                                MessageRepository messageRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("nimda"));
        admin.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123456"));

        Chat chat = new Chat();
        chat.setName("Testchat");

        Mono<Void> deleteAll = userRepository.deleteAll()
                .then(chatRepository.deleteAll())
                .then(messageRepository.deleteAll());

        Mono<Void> saveUsers = userRepository.saveAll(List.of(admin, user))
                .collectList()
                .doOnNext(users -> chat.setMembers(Set.copyOf(users)))
                .then();

        Mono<Void> saveChat = chatRepository.save(chat)
                .doOnNext(c -> admin.addAuthority(new ChatAuthority(c.getId())))
                .flatMap(c -> userRepository.save(admin))
                .then();

        // TODO add messages

        deleteAll
                .then(saveUsers)
                .then(saveChat)
                .subscribe();
    }
}
