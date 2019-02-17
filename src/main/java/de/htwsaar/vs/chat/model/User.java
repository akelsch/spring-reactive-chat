package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.htwsaar.vs.chat.auth.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * User object model (MongoDB document).
 *
 * @author Arthur Kelsch
 */
@Data
@Document
public class User {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String username;

    @NotBlank
    @JsonProperty(access = WRITE_ONLY)
    private String password;

    private List<Role> roles = new ArrayList<>();

    private List<GrantedAuthority> authorities = new ArrayList<>();

    public User() {
        addRole(Role.USER);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }
}
