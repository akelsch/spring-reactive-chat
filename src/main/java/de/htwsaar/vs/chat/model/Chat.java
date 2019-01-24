package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * chat
 *
 * @author Niklas Reinhard
 */
@Data
@NoArgsConstructor
@Document
public class Chat {
    @Id
    private String id;
    private Boolean isGroup;
    private String name;

    private List<String> memberIds;
    private List<String> admins;
}
