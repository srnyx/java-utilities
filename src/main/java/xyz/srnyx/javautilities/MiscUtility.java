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
     * If specific throwables are thrown by the {@link Supplier}, {@code null} is returned
     *
     * @param   supplier    the {@link Supplier} to execute
     * @param   throwables  the specific {@link Throwable}s to catch
     *
     * @return              the result of the {@link Supplier} or empty
     *
     * @param   <R>         the type of the result
     */
    @NotNull @SafeVarargs
    public static <R> Optional<R> handleException(@NotNull Supplier<R> supplier, @NotNull Class<? extends Throwable>... throwables) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (final Exception e) {
            for (final Class<? extends Throwable> throwable : throwables) if (throwable.isInstance(e)) return Optional.empty();
            throw e;
        }
    }

    /**
     * If an {@link Exception} is thrown by the {@link Supplier}, {@code null} is returned
     *
     * @param   supplier    the {@link Supplier} to execute
     *
     * @return              the result of the {@link Supplier} or empty
     *
     * @param   <R>         the type of the result
     */
    @NotNull
    public static <R> Optional<R> handleException(@NotNull Supplier<R> supplier) {
        return handleException(supplier, Exception.class);
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
