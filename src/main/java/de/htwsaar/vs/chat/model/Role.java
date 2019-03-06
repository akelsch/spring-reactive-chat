package de.htwsaar.vs.chat.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;

/**
 * Role Object Model.
 *
 * @author Mahan Karimi
 * @see User
 */
@Data
public class Role {
    @NotBlank
    private String role;
}
