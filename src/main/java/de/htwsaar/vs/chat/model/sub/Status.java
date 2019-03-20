package de.htwsaar.vs.chat.model.sub;

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
public class Status {

    @NotBlank
    private String status;
}
