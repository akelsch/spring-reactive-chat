package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.htwsaar.vs.chat.auth.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
    @JsonIgnore
    private String password;
    
    private List<Role> roles;
}
