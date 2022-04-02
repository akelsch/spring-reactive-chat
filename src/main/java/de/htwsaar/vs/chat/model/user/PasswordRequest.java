package de.htwsaar.vs.chat.model.user;

import de.htwsaar.vs.chat.model.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Password object model.
 *
 * @author Mahan Karimi
 * @see User
 */
@Data
public class PasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
