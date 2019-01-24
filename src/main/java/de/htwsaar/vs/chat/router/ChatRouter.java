package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * API routes starting with {@code /messages}.
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
                .andRoute(POST("/")
                        .and(accept(APPLICATION_JSON)), chatHandler::createChat);

        return RouterFunctions.nest(path("/api/v1/chats"), chatRoutes);
    }

//    @Bean
//    public RouterFunction<ServerResponse> routeMessages(MessageHandler messageHandler) {
//        RouterFunction<ServerResponse> messageRoutes = RouterFunctions
//                .route(GET("/{chatid}/messages")
//                        .and(accept(APPLICATION_JSON)), messageHandler::getAllForChat)
//                .andRoute(POST("/{chatid}/messages")
//                        .and(accept(APPLICATION_JSON)), messageHandler::addMessageToChat)
//                .andRoute(PUT("/{chatid}/messages/{messageid}")
//                        .and(accept(APPLICATION_JSON)), messageHandler::addMessageToChat)
//                .andRoute(DELETE("/{chatid}/messages/{messageid}")
//                        .and(accept(APPLICATION_JSON)), messageHandler::addMessageToChat);
//
//        return RouterFunctions.nest(path("/api/v1/chats/"), messageRoutes);
//    }
}
