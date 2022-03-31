package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Document(collection = "chats")
public class Chat extends BaseDocument {

    private String name;

    @NotNull
    @DBRef // TODO replace with manual references so non-reactive MongoDB dependency can be removed
    private Set<User> members;
}
