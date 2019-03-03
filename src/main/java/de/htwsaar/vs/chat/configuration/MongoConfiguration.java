package de.htwsaar.vs.chat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuration class for Spring Data MongoDB.
 *
 * @author Arthur Kelsch
 */
@EnableMongoAuditing
@Configuration
public class MongoConfiguration {
}
