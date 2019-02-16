package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.ChatHandler;
import de.htwsaar.vs.chat.handler.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * API routes starting with {@code /chats}.
 *
 * @author Niklas Reinhard
 */
@Configuration
public class ChatRouter {

    @Bean
    public RouterFunction<ServerResponse> routeChats(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> chatRoutes = RouterFunctions
                .route(GET("/")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAll)
                .andRoute(POST("/"), chatHandler::createChat);

        return RouterFunctions.nest(path("/api/v1/chats"), chatRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMembers(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> memberRoutes = RouterFunctions
                .route(GET("/members")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAllMembersForChat)
                .andRoute(POST("/members"), chatHandler::addMemberToChat)
                .andRoute(DELETE("/members/{userid}"), chatHandler::removeMemberFromChat);

        return RouterFunctions.nest(path("/api/v1/chats/{chatid}"), memberRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMessages(MessageHandler messageHandler) {
        RouterFunction<ServerResponse> messageRoutes = RouterFunctions
                .route(GET("/messages")
                        .and(accept(APPLICATION_JSON)), messageHandler::getAllMessagesForChat)
                .andRoute(GET("/messages/paginated")
                        .and(accept(APPLICATION_JSON)), messageHandler::getAllMessagesForChatPaginated)
                .andRoute(POST("/messages"), messageHandler::sendMessageToChat)
                .andRoute(DELETE("/messages/{messageid}"), messageHandler::deleteMessageFromChat);

        return RouterFunctions.nest(path("/api/v1/chats/{chatid}"), messageRoutes);
    }
}
