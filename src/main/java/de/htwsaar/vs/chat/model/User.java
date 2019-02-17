package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    @JsonProperty(access = WRITE_ONLY)
    private List<GrantedAuthority> roles = new ArrayList<>();

    @JsonProperty(access = WRITE_ONLY)
    private List<GrantedAuthority> authorities = new ArrayList<>();

    public User() {
        addRole(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public void addRole(GrantedAuthority role) {
        roles.add(role);
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }
}
