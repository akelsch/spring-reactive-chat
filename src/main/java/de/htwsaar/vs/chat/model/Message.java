package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.htwsaar.vs.chat.model.serializer.DocumentIdSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Message object model (MongoDB Document).
 *
 * @author Niklas Reinhard
 * @author Julian Quint
 */
@Data
@NoArgsConstructor
@Document
public class Message implements DocumentWithId {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    @NotNull
    @JsonSerialize(using = DocumentIdSerializer.class)
    @DBRef
    private Chat chat;

    @NotNull
    @JsonSerialize(using = DocumentIdSerializer.class)
    @DBRef
    private User sender;

    @NotBlank
    private String content;
}
