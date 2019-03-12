package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
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
                .route(GET("/")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAllChats)
                .andRoute(POST("/"), chatHandler::postChat)
                .andRoute(DELETE("/{chatid}"), chatHandler::deleteChat)
                .andRoute(GET("/stream")
                        .and(accept(TEXT_EVENT_STREAM)), chatHandler::getNewChats)
                .andRoute(GET("/messages/stream")
                        .and(accept(TEXT_EVENT_STREAM)), chatHandler::getNewMessages);

        return RouterFunctions.nest(path("/api/v1/chats"), chatRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMembers(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> memberRoutes = RouterFunctions
                .route(GET("/")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAllMembers)
                .andRoute(POST("/"), chatHandler::postMember)
                .andRoute(DELETE("/{userid}"), chatHandler::deleteMember);

        return RouterFunctions.nest(path("/api/v1/chats/{chatid}/members"), memberRoutes);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMessages(ChatHandler chatHandler) {
        RouterFunction<ServerResponse> messageRoutes = RouterFunctions
                .route(GET("/")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAllMessages)
                .andRoute(GET("/paginated")
                        .and(accept(APPLICATION_JSON)), chatHandler::getAllMessagesPaginated)
                .andRoute(POST("/"), chatHandler::postMessage)
                .andRoute(DELETE("/{messageid}"), chatHandler::deleteMessage);

        return RouterFunctions.nest(path("/api/v1/chats/{chatid}/messages"), messageRoutes);
    }
}
