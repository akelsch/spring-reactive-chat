package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Chat object model (MongoDB Document).
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Data
@NoArgsConstructor
@Document
public class Chat {

    @Id
    private String id;

    @NotBlank
    private String name;

    // TODO keep serializing members even though we have a /members sub-route?
    //  idea: link to /members as well as /messages instead (like Spring HATEOAS with HAL)
    @NotNull
    @DBRef
    private Set<User> members;
}
