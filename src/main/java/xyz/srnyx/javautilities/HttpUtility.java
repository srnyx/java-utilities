package xyz.srnyx.javautilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.javautilities.http.Http;
import xyz.srnyx.javautilities.http.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Utility class for making HTTP requests
 *
 * @deprecated  Use {@link Http} instead
 */
@Deprecated
public class HttpUtility {
    /**
     * Debug mode (enabled stack traces and 404 messages logged to console)
     */
    public static boolean DEBUG = false;

    /**
     * Sends a GET request to the specified URL and returns the result of the specified function
     *
     * @param   userAgent           the user agent to use
     * @param   url                 the URL to request from
     * @param   function            the function to apply to the {@link InputStreamReader}
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @param   <T>                 the type of the result of the specified function
     *
     * @return                      the result of the specified function, or null if the request failed
     */
    @NotNull
    public static <T> Optional<T> get(@NotNull String userAgent, @NotNull String url, @NotNull Function<InputStreamReader, T> function, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        return new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .get(url, connectionConsumer)
                .getBody()
                .map(body -> {
                    try {
                        return function.apply(new InputStreamReader(new ByteArrayInputStream(body.getBytes())));
                    } catch (final Exception e) {
                        if (DEBUG) e.printStackTrace();
                        return null;
                    }
                });
    }

    /**
     * Sends a GET request to the specified URL and returns the result as a {@link String}
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to request from
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      the {@link String}, or null if the request failed
     */
    @NotNull
    public static Optional<String> getString(@NotNull String userAgent, @NotNull String urlString, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        return new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .get(urlString, connectionConsumer)
                .getBody();
    }

    /**
     * Sends a GET request to the specified URL and returns the result as a {@link JsonElement}
     *
     * @param   userAgent           the user agent to use when retrieving the {@link JsonElement}
     * @param   urlString           the URL to retrieve the {@link JsonElement} from
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      the {@link JsonElement} retrieved from the specified URL
     */
    @NotNull
    public static Optional<JsonElement> getJson(@NotNull String userAgent, @NotNull String urlString, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        return new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .get(urlString, connection -> {
                    connection.setRequestProperty("Accept", "application/json");
                    if (connectionConsumer != null) connectionConsumer.accept(connection);
                })
                .getBodyAsJson();
    }

    /**
     * Sends a POST request to the specified URL with the specified data
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to send the POST request to
     * @param   data                the data to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public static Optional<Response> post(@NotNull String userAgent, @NotNull String urlString, byte @Nullable [] data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        final Response response = new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .post(urlString, data, connectionConsumer);
        if (response.requestFailed() || response.isCodeFailure()) return Optional.empty();
        return Optional.of(response);
    }

    /**
     * Sends a POST request to the specified URL with the specified {@link JsonObject JSON data}
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to send the POST request to
     * @param   data                the {@link JsonObject JSON data} to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public static Optional<Response> postJson(@NotNull String userAgent, @NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        return post(userAgent, urlString, data != null ? data.toString().getBytes() : null, connection -> {
            connection.setRequestProperty("Content-Type", "application/json");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
        });
    }

    /**
     * Sends a POST request to the specified URL with the specified form data
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to send the POST request to
     * @param   formData            the form data to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public static Optional<Response> postFormUrlEncoded(@NotNull String userAgent, @NotNull String urlString, @NotNull Map<String, String> formData, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        final Response response = new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .postFormUrlEncoded(urlString, formData, connectionConsumer);
        if (response.requestFailed() || response.isCodeFailure()) return Optional.empty();
        return Optional.of(response);
    }

    /**
     * Sends a PUT request to the specified URL with the specified {@link JsonElement JSON data}
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to send the PUT request to
     * @param   data                the {@link JsonElement JSON data} to send with the PUT request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public static Optional<Response> putJson(@NotNull String userAgent, @NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        final Response response = new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .putJson(urlString, data, connectionConsumer);
        if (response.requestFailed() || response.isCodeFailure()) return Optional.empty();
        return Optional.of(response);
    }

    /**
     * Sends a DELETE request to the specified URL
     *
     * @param   userAgent           the user agent to use
     * @param   urlString           the URL to send the DELETE request to
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public static Optional<Response> delete(@NotNull String userAgent, @NotNull String urlString, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        final Response response = new Http.Builder()
                .userAgent(userAgent)
                .debug(DEBUG)
                .build()
                .delete(urlString, connectionConsumer);
        if (response.requestFailed() || response.isCodeFailure()) return Optional.empty();
        return Optional.of(response);
    }

    /**
     * Constructs a new {@link HttpUtility} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private HttpUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
