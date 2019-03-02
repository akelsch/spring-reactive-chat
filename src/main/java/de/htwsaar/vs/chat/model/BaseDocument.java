package de.htwsaar.vs.chat.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Base MongoDB document for all other document classes.
 *
 * @author Arthur Kelsch
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class BaseDocument {

    @Id
    private String id;
}
