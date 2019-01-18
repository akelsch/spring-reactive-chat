package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UserHandler userHandler) {
        RouterFunction<ServerResponse> userRoutes = RouterFunctions
                .route(GET("/")
                        .and(accept(APPLICATION_JSON)), userHandler::getUsers)
                .andRoute(GET("/{uid}")
                        .and(accept(APPLICATION_JSON)), userHandler::getUser);

        return RouterFunctions.nest(path("/api/v1/users"), userRoutes);
    }
}
