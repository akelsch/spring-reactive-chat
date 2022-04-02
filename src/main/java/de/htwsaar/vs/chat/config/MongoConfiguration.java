package de.htwsaar.vs.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * Configuration class for Spring Data MongoDB.
 *
 * @author Arthur Kelsch
 */
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfiguration {
}
