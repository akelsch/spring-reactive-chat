package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

/**
 * User object model (MongoDB document).
 *
 * @author Arthur Kelsch
 */
@Data
@NoArgsConstructor
@Document
public class User {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String username;

    @NotBlank
    private String password;
}
