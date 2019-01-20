package de.htwsaar.vs.chat.router;

import de.htwsaar.vs.chat.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Routes starting with {@code /auth}.
 *
 * @author Arthur Kelsch
 */
@Configuration
public class AuthRouter {

    @Bean
    public RouterFunction<ServerResponse> routeAuth(AuthHandler authHandler) {
        RouterFunction<ServerResponse> authRoutes = RouterFunctions
                .route(POST("/signup")
                        .and(accept(APPLICATION_JSON)), authHandler::signup)
                .andRoute(POST("/signin")
                        .and(accept(APPLICATION_JSON)), authHandler::signin);

        return RouterFunctions.nest(path("/auth"), authRoutes);
    }
}
