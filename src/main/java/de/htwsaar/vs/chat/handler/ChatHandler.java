package de.htwsaar.vs.chat.handler;

import com.mongodb.DuplicateKeyException;
import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.ChatRouter;
import de.htwsaar.vs.chat.service.ChatService;
import de.htwsaar.vs.chat.service.MessageService;
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
 * @author Julian Quint
 */
@Component
public class ChatHandler {

    private final ChatService chatService;
    private final MessageService messageService;

    @Autowired
    public ChatHandler(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(chatService.findAllForUser(), Chat.class);
    }

    public Mono<ServerResponse> createChat(ServerRequest request) {
        return request
                .bodyToMono(Chat.class)
                .flatMap(chatService::save)
                .flatMap(chat -> ServerResponse.created(URI.create("/api/v1/chats/" + chat.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }

    public Mono<ServerResponse> getAllMembersForChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(chatService.findAllMembersForChat(chatId), User.class);
    }

    public Mono<ServerResponse> addMemberToChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return request
                .bodyToMono(User.class)
                .flatMap(member -> chatService.saveNewMember(chatId, member))
                .flatMap(chat -> ServerResponse.created(URI.create("/api/v1/chats/" + chat.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }

    public Mono<ServerResponse> removeMemberFromChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");
        String userId = request.pathVariable("userid");

        return chatService
                .removeMember(chatId, userId)
                .flatMap(signal -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getAllMessagesForChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(messageService.findAllMessagesForChat(chatId), Message.class);
    }

    public Mono<ServerResponse> getAllMessagesForChatPaginated(ServerRequest request) {
        String chatId = request.pathVariable("chatid");
        String start = request.queryParam("start").orElse("1");
        String chunk = request.queryParam("chunk").orElse("50");

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(messageService.findAllMessagesForChatPaginated(chatId, start, chunk), Message.class);
    }

    public Mono<ServerResponse> sendMessageToChat(ServerRequest request) {
        String chatId = request.pathVariable("chatid");

        return request
                .bodyToMono(Message.class)
                .flatMap(message -> messageService.addMessageToChat(message, chatId))
                .flatMap(message -> ServerResponse.created(URI.create("/api/v1/chats/" + chatId + "/messages/" + message.getId())).build())
                .onErrorResume(DecodingException.class, ResponseError::badRequest)
                .onErrorResume(ConstraintViolationException.class, ResponseError::badRequest)
                .onErrorResume(DuplicateKeyException.class, ResponseError::conflict);
    }

    public Mono<ServerResponse> deleteMessageFromChat(ServerRequest request) {
        String messageId = request.pathVariable("messageid");

        return ServerResponse.noContent()
                .build(messageService.delete(messageId));
    }
}
