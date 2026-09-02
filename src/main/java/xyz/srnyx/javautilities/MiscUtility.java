package xyz.srnyx.javautilities;

import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * General utility methods
 */
public class MiscUtility {
    /**
     * A {@link Random} instance
     */
    @NotNull public static final Random RANDOM = new Random();
    /**
     * A {@link JsonParser} instance
     */
    @NotNull public static final JsonParser JSON_PARSER = new JsonParser();
    /**
     * The number of available CPU processors
     */
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    /**
     * A {@link ScheduledExecutorService} for CPU intensive tasks (heavy computation, such as mathematical calculations or data processing)
     */
    @NotNull public static final ScheduledExecutorService CPU_SCHEDULER = newLongLifeScheduler(AVAILABLE_PROCESSORS, "JU-CPU");
    /**
     * A {@link ScheduledExecutorService} for IO intensive tasks (waiting for external resources, such as reading/writing files, making network requests, or querying a database)
     */
    @NotNull public static final ScheduledExecutorService IO_SCHEDULER = newLongLifeScheduler(AVAILABLE_PROCESSORS * 2, "JU-IO");

    /**
     * Creates a new {@link ScheduledExecutorService} with the specified number of threads and a custom thread name prefix.
     * The threads are set as daemon threads, which means they will not prevent the JVM from exiting when the application is finished.
     * A shutdown hook is also added to gracefully shut down the scheduler when the application exits.
     *
     * @param   threads the number of threads in the pool
     * @param   name    the prefix for the thread names
     *
     * @return  a new {@link ScheduledExecutorService} instance
     */
    @NotNull
    public static ScheduledExecutorService newLongLifeScheduler(int threads, @NotNull String name) {
        final AtomicInteger counter = new AtomicInteger();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threads, r -> {
            final Thread thread = new Thread(r, name + "-" + counter.incrementAndGet());
            thread.setDaemon(true); // Daemon threads are automatically terminated when JVM exits
            return thread;
        });

        // Prevents scheduler from executing existing delayed tasks after shutdown
        ((ScheduledThreadPoolExecutor) scheduler).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        // Shutdown hook to gracefully shut down scheduler when application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }, name + "-ShutdownHook"));

        return scheduler;
    }

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
