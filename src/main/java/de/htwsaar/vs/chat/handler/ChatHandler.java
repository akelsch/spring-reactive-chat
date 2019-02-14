package de.htwsaar.vs.chat.handler;

import com.auth0.jwt.JWT;
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

import static de.htwsaar.vs.chat.util.JwtUtil.JWT_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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
        String token = request.exchange().getRequest().getHeaders().getFirst(AUTHORIZATION)
                .substring(JWT_PREFIX.length());

        String uid = JWT.decode(token).getSubject();

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

    public Mono<ServerResponse> getAllMembersForChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(chatService.findAllMembersForChat(chatId), Chat.Member.class);
    }

    public Mono<ServerResponse> addMemberToChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return request
                .bodyToMono(Chat.Member.class)
                .flatMap(member -> chatService.saveNewMember(chatId, member))
                .flatMap(chat -> ServerResponse.created(URI.create("/chats/" + chat.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }

    public Mono<ServerResponse> removeMemberFromChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");
        String userId = request.pathVariable("userid");

        return ServerResponse.noContent()
                .build(chatService.removeMember(chatId, userId));
    }
}
