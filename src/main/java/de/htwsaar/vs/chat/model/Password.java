package de.htwsaar.vs.chat.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Password Object Model.
 *
 * @author Mahan Karimi
 * @see User
 */
@Data
public class Password {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
