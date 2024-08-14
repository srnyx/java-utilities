package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
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
    @NotNull
    public static <R> Optional<R> handleException(@NotNull Supplier<R> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (final Exception ignored) {
            return Optional.empty();
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
