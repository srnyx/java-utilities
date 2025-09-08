package xyz.srnyx.javautilities.manipulation;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.javautilities.MiscUtility;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        if (object == null) return Optional.empty();
        String string = object.toString();

        // Insert missing dashes if necessary
        if (!string.contains("-")) {
            if (string.length() != 32) return Optional.empty(); // Invalid UUID length
            try {
                string = string.substring(0, 8) + "-" + string.substring(8, 12) + "-" + string.substring(12, 16) + "-" + string.substring(16, 20) + "-" + string.substring(20);
            } catch (final IndexOutOfBoundsException e) {
                return Optional.empty(); // Invalid dash-less UUID format
            }
        }

        // Parse UUID
        final String finalString = string;
        return MiscUtility.handleException(() -> UUID.fromString(finalString), IllegalArgumentException.class);
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

    /**
     * Converts an {@link Object} to a {@link JsonElement}
     *
     * @param   object  the {@link Object} to convert
     *
     * @return          the converted {@link JsonElement} or {@link Optional#empty()} if parsing failed
     */
    @NotNull
    public static Optional<JsonElement> toJson(@Nullable Object object) {
        if (object == null) return Optional.of(JsonNull.INSTANCE);
        if (object instanceof JsonElement) return Optional.of((JsonElement) object);
        return MiscUtility.handleException(() -> MiscUtility.JSON_PARSER.parse(object.toString()), IllegalStateException.class);
    }

    /**
     * Converts a {@link JsonElement} to the specified subclass
     *
     * @param   element     the {@link JsonElement} to convert
     * @param   jsonClass   the subclass of {@link JsonElement} to convert to
     *
     * @return              an {@link Optional} containing the converted {@link JsonElement} if it is of the specified subclass, otherwise {@link Optional#empty()}
     *
     * @param   <T>         the type of the {@link JsonElement} subclass
     */
    @NotNull
    public static <T extends JsonElement> Optional<T> convertJsonElement(@Nullable JsonElement element, @NotNull Class<T> jsonClass) {
        return !jsonClass.isInstance(element) ? Optional.empty() : Optional.of(jsonClass.cast(element));
    }

    /**
     * Converts a {@link JsonPrimitive} to the specified primitive wrapper class
     *
     * @param   primitive       the {@link JsonPrimitive} to convert
     * @param   primitiveClass  the primitive wrapper {@link Class} to convert to
     *
     * @return                  an {@link Optional} containing the converted value if successful, otherwise {@link Optional#empty()}
     *
     * @param   <T>             the type of the primitive wrapper class
     */
    @NotNull
    public static <T> Optional<T> convertJsonPrimitive(@Nullable JsonPrimitive primitive, @NotNull Class<T> primitiveClass) {
        // Check if primitive is null or class not supported
        if (primitive == null || (
                primitiveClass != Boolean.class &&
                primitiveClass != Number.class &&
                primitiveClass != String.class &&
                primitiveClass != Double.class &&
                primitiveClass != BigDecimal.class &&
                primitiveClass != BigInteger.class &&
                primitiveClass != Float.class &&
                primitiveClass != Long.class &&
                primitiveClass != Short.class &&
                primitiveClass != Integer.class &&
                primitiveClass != Byte.class &&
                primitiveClass != Character.class)) {
            return Optional.empty();
        }

        // Handle conversion based on the primitive class type
        return MiscUtility.handleException(() -> {
            if (primitiveClass == Boolean.class) return primitive.getAsBoolean();
            if (primitiveClass == Number.class) return primitive.getAsNumber();
            if (primitiveClass == String.class) return primitive.getAsString();
            if (primitiveClass == Double.class) return primitive.getAsDouble();
            if (primitiveClass == BigDecimal.class) return primitive.getAsBigDecimal();
            if (primitiveClass == BigInteger.class) return primitive.getAsBigInteger();
            if (primitiveClass == Float.class) return primitive.getAsFloat();
            if (primitiveClass == Long.class) return primitive.getAsLong();
            if (primitiveClass == Short.class) return primitive.getAsShort();
            if (primitiveClass == Integer.class) return primitive.getAsInt();
            if (primitiveClass == Byte.class) return primitive.getAsByte();
            return primitive.getAsCharacter();
        }, IllegalStateException.class).map(primitiveClass::cast);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Mapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
