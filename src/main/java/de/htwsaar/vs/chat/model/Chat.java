package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.htwsaar.vs.chat.model.serializer.CollectionSizeSerializer;
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
public class Chat implements DocumentWithId {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotNull
    @JsonSerialize(using = CollectionSizeSerializer.class)
    @DBRef
    private Set<User> members;
}
