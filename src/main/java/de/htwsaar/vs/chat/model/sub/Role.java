package de.htwsaar.vs.chat.model.sub;

import de.htwsaar.vs.chat.model.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Role object model.
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
