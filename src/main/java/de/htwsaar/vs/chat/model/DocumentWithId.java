package de.htwsaar.vs.chat.model;

/**
 * Describes an object with an {@code id} field and its getters/setters.
 * <p>
 * This would have been an abstract class containing the field itself but
 * inheritance in Spring Data MongoDB sucks.
 *
 * @author Arthur Kelsch
 */
public interface DocumentWithId {

    String getId();

    void setId(String id);
}
