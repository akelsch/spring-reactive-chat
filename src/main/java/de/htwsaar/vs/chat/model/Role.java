package de.htwsaar.vs.chat.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Role Object Model.
 *
 * @author Mahan Karimi
 * @see User
 */
@Data
public class Role {

    @NotBlank
    @Pattern(regexp = "ROLE_\\w+")
    private String role;
}
