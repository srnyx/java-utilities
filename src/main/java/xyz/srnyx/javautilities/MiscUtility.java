package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * General utility methods
 */
public class MiscUtility {
    /**
     * If an exception is thrown by the {@link Supplier}, {@code null} is returned
     *
     * @param   supplier    the {@link Supplier} to execute
     *
     * @return              the result of the {@link Supplier} or {@code null}
     *
     * @param   <R>         the type of the result
     */
    @Nullable
    public static <R> R handleException(@NotNull Supplier<R> supplier) {
        return handleException(supplier, null);
    }

    /**
     * If an exception is thrown by the {@link Supplier}, the default value is returned
     *
     * @param   supplier    the {@link Supplier} to execute
     * @param   def         the default value to return if an exception is thrown
     *
     * @return              the result of the {@link Supplier} or the default value
     *
     * @param   <R>         the type of the result
     */
    @Nullable
    public static <R> R handleException(@NotNull Supplier<R> supplier, @Nullable R def) {
        try {
            return supplier.get();
        } catch (final Exception e) {
            return def;
        }
    }

    /**
     * Gets a {@link Set} of all the enum's value's names
     *
     * @param   enumClass   the enum class to get the names from
     *
     * @return              the {@link Set} of the enum's value's names
     */
    @NotNull
    public static Set<String> getEnumNames(@NotNull Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    /**
     * Constructs a new {@link MiscUtility} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private MiscUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
