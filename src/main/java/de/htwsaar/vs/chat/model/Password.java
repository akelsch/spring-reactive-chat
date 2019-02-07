package de.htwsaar.vs.chat.model;

import lombok.Data;

/**
 * Password Object Model (MongoDB document).
 *
 * @author Mahan Karimi
 */
@Data
public class Password {

    private String oldPassword;
    private String newPassword;
}

/*
{
    "oldPassword": "altes Passwrot",
    "newPassword": "neues Paswort
}
 */