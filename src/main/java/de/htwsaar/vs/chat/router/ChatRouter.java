package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * API routes starting with {@code /chats}.
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Configuration
public class ChatRouter {

    @Bean
    public RouterFunction<ServerResponse> routeChats(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> chatRoutes = RouterFunctions
                .route(GET("").and(accept(MediaType.APPLICATION_JSON)), chatHandler::getAllChats)
                .andRoute(POST(""), chatHandler::postChat)
                .andRoute(DELETE("/{chatId}"), chatHandler::deleteChat)
                .andRoute(GET("/stream").and(accept(MediaType.TEXT_EVENT_STREAM)), chatHandler::getNewChats)
                .andRoute(GET("/messages/stream").and(accept(MediaType.TEXT_EVENT_STREAM)), chatHandler::getNewMessages);

        return RouterFunctions.nest(path("/api/v1/chats"), chatRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMembers(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> memberRoutes = RouterFunctions
                .route(GET("").and(accept(MediaType.APPLICATION_JSON)), chatHandler::getAllMembers)
                .andRoute(POST(""), chatHandler::postMember)
                .andRoute(DELETE("/{userId}"), chatHandler::deleteMember);

        return RouterFunctions.nest(path("/api/v1/chats/{chatId}/members"), memberRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMessages(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> messageRoutes = RouterFunctions
                .route(GET("").and(accept(MediaType.APPLICATION_JSON)), chatHandler::getAllMessages)
                .andRoute(GET("/paginated").and(accept(MediaType.APPLICATION_JSON)), chatHandler::getAllMessagesPaginated)
                .andRoute(POST(""), chatHandler::postMessage)
                .andRoute(DELETE("/{messageId}"), chatHandler::deleteMessage);

        return RouterFunctions.nest(path("/api/v1/chats/{chatId}/messages"), messageRoutes);
    }
}
