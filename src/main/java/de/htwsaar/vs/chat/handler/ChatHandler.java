package de.htwsaar.vs.chat.handler;

import de.htwsaar.vs.chat.model.Chat;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.model.User;
import de.htwsaar.vs.chat.router.ChatRouter;
import de.htwsaar.vs.chat.service.ChatService;
import de.htwsaar.vs.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Handler methods for {@link ChatRouter}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Component
@RequiredArgsConstructor
public class ChatHandler {

    private final ChatService chatService;
    private final MessageService messageService;

    public Mono<ServerResponse> getAllChats(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatService.findAllChatsForCurrentUser(), Chat.class);
    }

    public Mono<ServerResponse> postChat(ServerRequest request) {
        return request
                .bodyToMono(Chat.class)
                .flatMap(chatService::saveChat)
                .flatMap(chat -> ServerResponse.created(URI.create("/api/v1/chats/" + chat.getId())).build());
    }

    public Mono<ServerResponse> deleteChat(ServerRequest request) {
        String chatId = request.pathVariable("chatId");

        return chatService
                .deleteChat(chatId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getNewChats(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(chatService.streamNewChats(), Chat.class);
    }

    public Mono<ServerResponse> getNewMessages(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(chatService.streamNewMessages(), Message.class);
    }

    public Mono<ServerResponse> getAllMembers(ServerRequest request) {
        String chatId = request.pathVariable("chatId");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chatService.findAllMembers(chatId), User.class);
    }

    public Mono<ServerResponse> postMember(ServerRequest request) {
        String chatId = request.pathVariable("chatId");

        return request
                .bodyToMono(User.class)
                .flatMap(member -> chatService.saveMember(chatId, member))
                .flatMap(chat -> ServerResponse.created(URI.create("/api/v1/chats/" + chat.getId())).build());
    }

    public Mono<ServerResponse> deleteMember(ServerRequest request) {
        String chatId = request.pathVariable("chatId");
        String userId = request.pathVariable("userId");

        return chatService
                .deleteMember(chatId, userId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getAllMessages(ServerRequest request) {
        String chatId = request.pathVariable("chatId");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageService.findAllMessages(chatId), Message.class);
    }

    public Mono<ServerResponse> getAllMessagesPaginated(ServerRequest request) {
        String chatId = request.pathVariable("chatId");
        String start = request.queryParam("start").orElse("1");
        String chunk = request.queryParam("chunk").orElse("50");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageService.findAllMessagesPaginated(chatId, start, chunk), Message.class);
    }

    public Mono<ServerResponse> postMessage(ServerRequest request) {
        String chatId = request.pathVariable("chatId");

        return request
                .bodyToMono(Message.class)
                .flatMap(message -> messageService.saveMessage(message, chatId))
                .flatMap(message -> ServerResponse.created(URI.create("/api/v1/chats/" + chatId + "/messages/" + message.getId())).build());
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest request) {
        String messageId = request.pathVariable("messageId");

        return messageService
                .findById(messageId)
                .flatMap(messageService::deleteMessage)
                .then(ServerResponse.noContent().build());
    }
}
