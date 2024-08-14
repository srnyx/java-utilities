package xyz.srnyx.javautilities.manipulation;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A utility class for parsing {@link Duration durations} from strings
 */
public class DurationParser {
    @NotNull private static final Duration[] DURATIONS = {
            ChronoUnit.YEARS.getDuration(),
            ChronoUnit.MONTHS.getDuration(),
            ChronoUnit.WEEKS.getDuration(),
            ChronoUnit.DAYS.getDuration(),
            ChronoUnit.HOURS.getDuration(),
            ChronoUnit.MINUTES.getDuration(),
            ChronoUnit.SECONDS.getDuration()};
    private static final int DURATIONS_LENGTH = DURATIONS.length;
    @NotNull private static final Pattern PATTERN = Pattern.compile(Stream.of(
            "y(?:ear)?s?",
                    "mo(?:nth)?s?",
                    "w(?:eek)?s?",
                    "d(?:ay)?s?",
                    "h(?:our|r)?s?",
                    "m(?:inute|in)?s?",
                    "s(?:econd|ec)?s?")
            .map(pattern -> "(?:(\\d+)\\s*" + pattern + "[,\\s]*)?")
            .collect(Collectors.joining("", "^\\s*", "$")), Pattern.CASE_INSENSITIVE);

    /**
     * Parses a {@link Duration} from a string
     *
     * @param   input   the string to parse
     *
     * @return          the parsed duration, or {@code null} if the string is invalid
     */
    @NotNull
    public static Optional<Duration> parse(@NotNull String input) {
        final Matcher matcher = PATTERN.matcher(input);
        if (!matcher.matches()) return Optional.empty();

        Duration duration = Duration.ZERO;
        for (int i = 0; i < DURATIONS_LENGTH; i++) {
            final String group = matcher.group(i + 1);
            if (group != null && !group.isEmpty()) {
                final Optional<Integer> number = Mapper.toInt(group);
                if (number.isPresent()) duration = duration.plus(DURATIONS[i].multipliedBy(number.get()));
            }
        }

        return Optional.of(duration);
    }

    private DurationParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
