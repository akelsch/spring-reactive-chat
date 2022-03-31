package de.htwsaar.vs.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * User object model (MongoDB document).
 *
 * @author Arthur Kelsch
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Document(collection = "users")
public class User extends BaseDocument {

    @NotBlank
    @Indexed(unique = true)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private List<GrantedAuthority> roles = new ArrayList<>();

    @JsonIgnore
    private List<GrantedAuthority> authorities = new ArrayList<>();

    private String status = "";

    public User() {
        addRole(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public void addRole(GrantedAuthority role) {
        roles.add(role);
    }

    public boolean removeRole(GrantedAuthority role) {
        return roles.remove(role);
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }

    public boolean removeAuthority(GrantedAuthority authority) {
        return authorities.remove(authority);
    }
}
