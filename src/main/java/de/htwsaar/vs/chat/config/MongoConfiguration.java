package de.htwsaar.vs.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;

import javax.validation.Validator;

/**
 * Configuration class for Spring Data MongoDB.
 *
 * @author Arthur Kelsch
 */
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfiguration {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(Validator validator) {
        return new ValidatingMongoEventListener(validator);
    }
}
