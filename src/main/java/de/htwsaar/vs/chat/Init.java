package de.htwsaar.vs.chat;

import de.htwsaar.vs.chat.auth.Role;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.repository.ChatRepository;
import de.htwsaar.vs.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

import static java.util.Arrays.asList;

@Component
public class Init implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Init(UserRepository userRepository, ChatRepository chatRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("nimda"));
        admin.addRole(Role.ADMIN);

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123456"));

        Chat chat = new Chat();
        chat.setName("Testchat");
        chat.setMembers(new HashSet<>(asList(admin, user)));

        userRepository.deleteAll()
                .then(chatRepository.deleteAll())
                .thenMany(userRepository.saveAll(asList(admin, user)))
                .then(chatRepository.save(chat))
                .subscribe();
    }
}
