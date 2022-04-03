package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * API routes starting with {@code /users}.
 *
 * @author Arthur Kelsch
 * @author Mahan Karimi
 * @author Julian Quint
 */
@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UserHandler userHandler) {
        RouterFunction<ServerResponse> userRoutes = RouterFunctions
                .route(GET("").and(accept(MediaType.APPLICATION_JSON)), userHandler::getAll)
                .andRoute(GET("/{uid}").and(accept(MediaType.APPLICATION_JSON)), userHandler::get)
                .andRoute(DELETE("/{uid}"), userHandler::delete)
                .andRoute(POST("/{uid}/change_password"), userHandler::changePassword)
                .andRoute(PUT("/{uid}/roles"), userHandler::putRole)
                .andRoute(DELETE("/{uid}/roles"), userHandler::deleteRole)
                .andRoute(PUT("/{uid}/status"), userHandler::putStatus)
                .andRoute(DELETE("/{uid}/status"), userHandler::deleteStatus);

        return RouterFunctions.nest(path("/api/v1/users"), userRoutes);
    }
}
