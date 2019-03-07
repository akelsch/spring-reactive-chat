package de.htwsaar.vs.chat.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Chat object model (MongoDB Document).
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Getter
@Setter
@ToString
@Document
public class Chat extends BaseDocument {

    private String name;

    @NotNull
    @DBRef
    private Set<User> members;
}
