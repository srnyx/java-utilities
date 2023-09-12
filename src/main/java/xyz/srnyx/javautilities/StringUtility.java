package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collections;


/**
 * General utility methods for {@link String}s and anything else related
 */
public class StringUtility {
    /**
     * Repeats a {@link CharSequence} a given amount of times
     *
     * @param   charSequence    the {@link CharSequence} to repeat
     * @param   amount          the amount of times to repeat the {@link CharSequence}
     *
     * @return                  the repeated {@link CharSequence}
     */
    @NotNull
    public static String repeat(@NotNull CharSequence charSequence, int amount) {
        return String.join("", Collections.nCopies(amount, charSequence));
    }

    /**
     * Formats a {@link Double} value using the given pattern
     *
     * @param   value   the {@link Number} to format
     * @param   pattern the pattern to use
     *
     * @return          the formatted value
     */
    @NotNull
    public static String formatNumber(@NotNull Number value, @Nullable String pattern) {
        if (pattern == null) pattern = "#,###.##";
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * Shortens a string to a given length, adding "..." at the end ("..." is included in the length)
     *
     * @param   string  the string to shorten
     * @param   length  the length to shorten to
     *
     * @return          the shortened string
     */
    @NotNull
    public static String shorten(@NotNull String string, int length) {
        return string.length() + 3 > length ? string.substring(0, length - 3) + "..." : string;
    }

    /**
     * Constructs a new {@link StringUtility} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private StringUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
