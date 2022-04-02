package de.htwsaar.vs.chat.model.user;

import de.htwsaar.vs.chat.model.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Status object model.
 *
 * @author Mahan Karimi
 * @see User
 */
@Data
public class StatusRequest {

    @NotBlank
    private String status;
}
