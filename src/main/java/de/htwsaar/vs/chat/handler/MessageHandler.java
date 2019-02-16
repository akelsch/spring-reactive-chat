package de.htwsaar.vs.chat.handler;

import com.mongodb.DuplicateKeyException;
import de.htwsaar.vs.chat.model.Message;
import de.htwsaar.vs.chat.router.ChatRouter;
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
 */
@Component
public class MessageHandler {

    private final MessageService messageService;

    @Autowired
    public MessageHandler(MessageService messageService) {
        this.messageService = messageService;
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
