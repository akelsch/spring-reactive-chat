package de.htwsaar.vs.chat.handler;

import com.mongodb.DuplicateKeyException;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.router.ChatRouter;
import de.htwsaar.vs.chat.service.ChatService;
import de.htwsaar.vs.chat.util.ResponseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Handler methods for {@link ChatRouter}.
 *
 * @author Niklas Reinhard
 */
@Component
public class ChatHandler {

    private final ChatService chatService;

    @Autowired
    public ChatHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        // TODO: don't parse uid from uid param but jwt token
        String uid = request.queryParam("uid").orElse("");
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(chatService.findAllForUser(uid), Chat.class);
    }

    public Mono<ServerResponse> createChat(ServerRequest request) {
        return request
                .bodyToMono(Chat.class)
                .flatMap(chatService::save)
                .flatMap(chat -> ServerResponse.created(URI.create("/chats/" + chat.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }
}
