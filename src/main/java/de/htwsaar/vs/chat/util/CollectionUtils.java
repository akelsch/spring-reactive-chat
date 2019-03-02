package de.htwsaar.vs.chat.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class providing methods to work with {@link Collection} classes.
 *
 * @author Arthur Kelsch
 */
@UtilityClass
public final class CollectionUtils {

    /**
     * Returns an (mutable) empty {@link HashSet} if given set is {@code null},
     * the set itself otherwise.
     *
     * @param set the set to null check
     * @param <T> the set type
     * @return an empty set if given set is {@code null}
     */
    public static <T> Set<T> emptySetIfNull(final Set<T> set) {
        return set == null ? new HashSet<>() : set;
    }
}
