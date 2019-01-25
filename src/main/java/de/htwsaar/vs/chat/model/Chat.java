package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Chat Model (MongoDB Document)
 *
 * @author Niklas Reinhard
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@Document
public class Chat {

    protected boolean canEqual(final Object other) {
        return other instanceof Chat;
    }

    @Data
    @NoArgsConstructor
    @Document
    public static class Member{
        @DBRef
        private User user;
        private Boolean isAdmin = false;
    }

    @Id
    private String id;
    private Boolean isGroup;
    private String name;
    private List<Member> members;
}
