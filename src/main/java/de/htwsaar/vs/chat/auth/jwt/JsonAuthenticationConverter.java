package de.htwsaar.vs.chat.auth.jwt;

import de.htwsaar.vs.chat.model.User;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JsonAuthenticationConverter implements ServerAuthenticationConverter {

    private final Decoder<?> decoder;

    public JsonAuthenticationConverter() {
        decoder = new Jackson2JsonDecoder();
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        Flux<DataBuffer> body = exchange.getRequest().getBody();

        return decoder
                .decodeToMono(body, ResolvableType.forType(User.class), MediaType.APPLICATION_JSON, Hints.none())
                .cast(User.class)
                .map(u -> new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()));
    }
}
