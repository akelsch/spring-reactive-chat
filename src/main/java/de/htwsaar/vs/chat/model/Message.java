package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * chat
 *
 * @author Niklas Reinhard
 */
@Data
@NoArgsConstructor
@Document
public class Message {
    @Version
    private Long version;
    @Id
    private String id;
    @DBRef
    private Chat chat;
    // only id needed
    @DBRef
    private User sender;
    private String content;
    @CreatedDate
    private LocalDateTime createdDate;
}
