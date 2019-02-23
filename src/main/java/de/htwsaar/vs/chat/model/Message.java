package de.htwsaar.vs.chat.model;

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
public class Message {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    // TODO serialize id/name only? link to chat?
    @NotNull
    @DBRef
    private Chat chat;

    // TODO serialize id/username only? link to user?
    @NotNull
    @DBRef
    private User sender;

    @NotBlank
    private String content;
}
