package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.auth.ChatAuthority;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.MessageRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;

import static java.util.Arrays.asList;

@Component
public class Init implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Init(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository,
                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
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

        Mono<Void> saveUsers = userRepository.saveAll(asList(admin, user))
                .collectList()
                .doOnNext(users -> chat.setMembers(new HashSet<>(users)))
                .then();

        Mono<Void> saveChat = chatRepository.save(chat)
                .doOnNext(c -> admin.addAuthority(new ChatAuthority(c.getId())))
                .flatMap(c -> userRepository.save(admin))
                .then();

        deleteAll
                .then(saveUsers)
                .then(saveChat)
                .subscribe();
    }
}
