package xyz.srnyx.javautilities.manipulation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.javautilities.MiscUtility;

import java.util.Optional;


/**
 * A utility class for converting {@link Object Objects} to other types
 */
public class Mapper {
    /**
     * Converts an {@link Object} to the specified {@link Class}
     *
     * @param   object  the {@link Object} to convert
     * @param   clazz   the {@link Class} to convert to
     *
     * @return          the converted {@link Object} or {@code null}
     *
     * @param   <T>     the type of the {@link Class}
     */
    @NotNull
    public static <T> Optional<T> to(@Nullable Object object, @NotNull Class<T> clazz) {
        return !clazz.isInstance(object) ? Optional.empty() : MiscUtility.handleException(() -> clazz.cast(object));
    }

    /**
     * Converts an {@link Object} to a {@link Integer}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Integer} or {@code null}
     */
    @NotNull
    public static Optional<Integer> toInt(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Integer.parseInt(object.toString()));
    }

    /**
     * Converts an {@link Object} to a {@link Double}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Double} or {@code null}
     */
    @NotNull
    public static Optional<Double> toDouble(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Double.parseDouble(object.toString()));
    }

    /**
     * Converts an {@link Object} to a {@link Long}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Long} or {@code null}
     */
    @NotNull
    public static Optional<Long> toLong(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Long.parseLong(object.toString()));
    }

    private Mapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
