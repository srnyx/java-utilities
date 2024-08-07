package xyz.srnyx.javautilities.manipulation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import xyz.srnyx.javautilities.StringUtility;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility class to format durations. This has the same (or at least similar) functionality as {@code org.apache.commons.lang.time.DurationFormatUtils}
 */
public class DurationFormatter {
    /**
     * Handles whether to pad the left hand side of numbers with 0's
     *
     * @param   padWithZeros    whether to pad the left hand side of the String with 0's
     * @param   integer         the int to pad out (will be converted to a {@link String})
     * @param   size            the size to pad to
     *
     * @return                  the padded String (or the original String if no padding was required)
     */
    @NotNull
    private static String padWithZeros(boolean padWithZeros, int integer, int size) {
        final String string = Integer.toString(integer);
        final int length = string.length();
        if (!padWithZeros || length >= size) return string;
        final int pads = size - length;
        if (pads > 8192) return string;
        return StringUtility.repeat("0", pads) + string;
    }

    /**
     * Formats the duration as a string, using the specified format and padding the left hand side of numbers with 0's
     *
     * @param   durationMillis  the duration to format
     * @param   format          the way in which to format the duration
     *
     * @return                  the duration as a String
     */
    @NotNull
    public static String formatDuration(long durationMillis, @NotNull String format) {
        return formatDuration(durationMillis, format, true);
    }

    /**
     * Formats the duration as a string, using the specified format
     *
     * @param   durationMillis  the duration to format
     * @param   format          the way in which to format the duration
     * @param   padWithZeros    whether to pad the left hand side of numbers with 0's
     *
     * @return                  the duration as a String
     */
    @NotNull
    public static String formatDuration(long durationMillis, @NotNull String format, boolean padWithZeros) {
        // Get tokens
        final char[] array = format.toCharArray();
        final List<Token> tokens = new ArrayList<>(array.length);
        boolean inLiteral = false;
        StringBuilder literalBuilder = null;
        Token previous = null;
        for (final char character : array) {
            // Literal
            if (inLiteral && character != '\'') {
                literalBuilder.append(character);
                continue;
            }

            switch (character) {
                case '\'':
                    // Literal
                    if (inLiteral) {
                        inLiteral = false;
                        literalBuilder = null;
                    } else {
                        inLiteral = true;
                        literalBuilder = new StringBuilder();
                        tokens.add(new Token(literalBuilder, true));
                    }
                    previous = null;
                    break;
                case 'y':
                case 'M':
                case 'd':
                case 'H':
                case 'm':
                case 's':
                case 'S':
                    literalBuilder = null;

                    // Same as previous token, increment count
                    final String value = String.valueOf(character);
                    if (previous != null && previous.value.toString().equals(value)) {
                        previous.count++;
                        break;
                    }

                    // New token
                    final Token token = new Token(new StringBuilder(value), false);
                    tokens.add(token);
                    previous = token;
                    break;
                default:
                    // Doesn't match any token, treat as literal
                    if (literalBuilder == null) {
                        literalBuilder = new StringBuilder();
                        tokens.add(new Token(literalBuilder, true));
                    }
                    literalBuilder.append(character);
                    previous = null;
            }
        }

        int years = 0;
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int milliseconds = 0;

        // years
        if (Token.containsTokenWithValue(tokens, "y")) {
            years = (int) (durationMillis / 31557600000L);
            durationMillis = durationMillis - (years * 31557600000L);
        }
        // months
        if (Token.containsTokenWithValue(tokens, "M")) {
            months = (int) (durationMillis / 2629800000L);
            durationMillis = durationMillis - (months * 2629800000L);
        }
        // days
        if (Token.containsTokenWithValue(tokens, "d")) {
            days = (int) (durationMillis / 86400000);
            durationMillis = durationMillis - (days * 86400000L);
        }
        // hours
        if (Token.containsTokenWithValue(tokens, "H")) {
            hours = (int) (durationMillis / 3600000);
            durationMillis = durationMillis - (hours * 3600000L);
        }
        // minutes
        if (Token.containsTokenWithValue(tokens, "m")) {
            minutes = (int) (durationMillis / 60000);
            durationMillis = durationMillis - (minutes * 60000L);
        }
        // seconds
        if (Token.containsTokenWithValue(tokens, "s")) {
            seconds = (int) (durationMillis / 1000);
            durationMillis = durationMillis - (seconds * 1000L);
        }
        // milliseconds
        if (Token.containsTokenWithValue(tokens, "S")) milliseconds = (int) durationMillis;

        final StringBuilder builder = new StringBuilder();
        boolean lastOutputSeconds = false;
        for (final Token token : tokens) {
            final String value = token.value.toString();

            // Literal
            if (token.literal) {
                builder.append(value);
                continue;
            }

            final int count = token.count;
            switch (value) {
                case "y":
                    builder.append(padWithZeros(padWithZeros, years, count));
                    lastOutputSeconds = false;
                    break;
                case "M":
                    builder.append(padWithZeros(padWithZeros, months, count));
                    lastOutputSeconds = false;
                    break;
                case "d":
                    builder.append(padWithZeros(padWithZeros, days, count));
                    lastOutputSeconds = false;
                    break;
                case "H":
                    builder.append(padWithZeros(padWithZeros, hours, count));
                    lastOutputSeconds = false;
                    break;
                case "m":
                    builder.append(padWithZeros(padWithZeros, minutes, count));
                    lastOutputSeconds = false;
                    break;
                case "s":
                    builder.append(padWithZeros(padWithZeros, seconds, count));
                    lastOutputSeconds = true;
                    break;
                case "S":
                    final String millisecondsString = padWithZeros(padWithZeros, milliseconds, count);
                    if (!lastOutputSeconds) {
                        builder.append(millisecondsString);
                        break;
                    }
                    milliseconds += 1000;
                    builder.append(millisecondsString.substring(1));
                    lastOutputSeconds = false;
                    break;
                default:
                    builder.append(value);
                    lastOutputSeconds = false;
                    break;
            }
        }

        return builder.toString();
    }

    /**
     * Constructs a new {@link DurationFormatter} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private DurationFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Element that is parsed from the format pattern
     */
    private static class Token {
        @NotNull private final StringBuilder value;
        private int count = 1;
        private final boolean literal;

        /**
         * Wraps a token around a value. A value would be something like a 'Y'
         *
         * @param   value   to wrap
         */
        private Token(@NotNull StringBuilder value, boolean literal) {
            this.value = value;
            this.literal = literal;
        }

        /**
         * Helper method to determine if a set of tokens contain a value
         *
         * @param   tokens  set to look in
         * @param   value   to look for
         *
         * @return          boolean <code>true</code> if contained
         */
        @Contract(pure = true)
        static boolean containsTokenWithValue(@NotNull List<Token> tokens, @NotNull String value) {
            for (final Token token : tokens) if (!token.literal && token.value.toString().equals(value)) return true;
            return false;
        }
    }
}
