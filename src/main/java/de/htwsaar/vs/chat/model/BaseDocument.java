package de.htwsaar.vs.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Base MongoDB document for all other document classes.
 *
 * @author Arthur Kelsch
 */
@Data
public abstract class BaseDocument {

    @Id
    private String id;
}
