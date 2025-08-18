package xyz.srnyx.javautilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Utility class for making HTTP requests
 */
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
        T result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            if (connection.getResponseCode() == 404) {
                if (DEBUG) System.out.println("[JU] 404: " + url);
                return Optional.empty();
            }
            result = function.apply(new InputStreamReader(connection.getInputStream()));
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return Optional.ofNullable(result);
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
        return get(userAgent, urlString, reader -> new BufferedReader(reader).lines().collect(Collectors.joining("\n")), connectionConsumer);
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
        return get(userAgent, urlString, reader -> new JsonParser().parse(reader), connection -> {
            connection.setRequestProperty("Accept", "application/json");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
        });
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
    public static Optional<Response> post(@NotNull String userAgent, @NotNull String urlString, @Nullable byte[] data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setDoOutput(true);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            if (data != null) connection.getOutputStream().write(data);
            response = new Response(connection.getResponseCode(), connection.getResponseMessage(), getResponseBody(connection));
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return Optional.ofNullable(response);
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
        final String formBody = formData.entrySet().stream()
                .map(entry -> {
                    try {
                        return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
                    } catch (final Exception e) {
                        if (DEBUG) e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("&"));
        return post(userAgent, urlString, formBody.getBytes(), connection -> {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
        });
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
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            if (data != null) connection.getOutputStream().write(data.toString().getBytes());
            response = new Response(connection.getResponseCode(), connection.getResponseMessage(), getResponseBody(connection));
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return Optional.ofNullable(response);
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
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            response = new Response(connection.getResponseCode(), connection.getResponseMessage(), getResponseBody(connection));
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return Optional.ofNullable(response);
    }

    /**
     * Gets the response body from the specified {@link HttpURLConnection}
     *
     * @param   connection  the {@link HttpURLConnection} to get the response body from
     *
     * @return              the response body, or null if an error occurred
     */
    @Nullable
    private static String getResponseBody(@NotNull HttpURLConnection connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        return null;
    }

    /**
     * Constructs a new {@link HttpUtility} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private HttpUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Represents the response from an HTTP request
     */
    public static class Response {
        /**
         * The HTTP response code
         */
        public final int code;
        /**
         * The HTTP response message
         */
        @Nullable public final String message;
        /**
         * The HTTP response body
         */
        @Nullable public final String body;

        /**
         * Constructs a new {@link Response} instance
         *
         * @param   code    {@link #code}
         * @param   message {@link #message}
         * @param   body    {@link #body}
         */
        public Response(int code, @Nullable String message, @Nullable String body) {
            this.code = code;
            this.message = message;
            this.body = body;
        }
    }
}
