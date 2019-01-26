package de.htwsaar.vs.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatApplicationIntegrationTest {

    ChatApplicationIntegrationTest() {
        System.out.println("--- Integration test ---");
    }

    @Test
    void contextLoads() {
    }
}
