package xyz.srnyx.javautilities.manipulation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.javautilities.MiscUtility;

import java.util.Optional;
import java.util.UUID;


/**
 * A utility class for converting {@link Object Objects} to other types
 */
public class Mapper {
    /**
     * Casts an {@link Object} to the specified {@link Class}
     *
     * @param   object  the {@link Object} to convert
     * @param   clazz   the {@link Class} to convert to
     *
     * @return          the converted {@link Object} or {@code Optional#empty()}
     *
     * @param   <T>     the type of the {@link Class}
     */
    @NotNull
    public static <T> Optional<T> to(@Nullable Object object, @NotNull Class<T> clazz) {
        return !clazz.isInstance(object) ? Optional.empty() : MiscUtility.handleException(() -> clazz.cast(object), ClassCastException.class);
    }

    /**
     * Converts an {@link Object} to a {@link Integer}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Integer} or {@link Optional#empty()}
     */
    @NotNull
    public static Optional<Integer> toInt(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Integer.parseInt(object.toString()), NumberFormatException.class);
    }

    /**
     * Converts an {@link Object} to a {@link Double}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Double} or {@link Optional#empty()}
     */
    @NotNull
    public static Optional<Double> toDouble(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Double.parseDouble(object.toString()), NumberFormatException.class);
    }

    /**
     * Converts an {@link Object} to a {@link Long}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link Long} or {@link Optional#empty()}
     */
    @NotNull
    public static Optional<Long> toLong(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> Long.parseLong(object.toString()), NumberFormatException.class);
    }

    /**
     * Converts an {@link Object} to a {@link UUID}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the {@link UUID} or {@link Optional#empty()}
     */
    @NotNull
    public static Optional<UUID> toUUID(@Nullable Object object) {
        return object == null ? Optional.empty() : MiscUtility.handleException(() -> UUID.fromString(object.toString()), IllegalArgumentException.class);
    }

    /**
     * Converts a {@link String} to an {@link Enum} value of the specified class
     *
     * @param   name        the name of the {@link Enum} value to convert
     * @param   enumClass   the {@link Class} of the {@link Enum} to convert to
     *
     * @return              an {@link Optional} containing the {@link Enum} value if it exists, otherwise {@link Optional#empty()}
     *
     * @param   <T>         the type of the {@link Enum}
     */
    @NotNull
    public static <T extends Enum<T>> Optional<T> toEnum(@Nullable String name, @NotNull Class<T> enumClass) {
        return name == null || name.isEmpty() ? Optional.empty() : MiscUtility.handleException(() -> Enum.valueOf(enumClass, name.toUpperCase()), IllegalArgumentException.class);
    }

    private Mapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
