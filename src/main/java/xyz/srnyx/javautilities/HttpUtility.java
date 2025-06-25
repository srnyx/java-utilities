package xyz.srnyx.javautilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Utility class for making HTTP requests
 */
public class HttpUtility {
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
        return get(userAgent, urlString, reader -> new JsonParser().parse(reader), connectionConsumer);
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
    @Nullable
    public static Response postJson(@NotNull String userAgent, @NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            if (data != null) connection.getOutputStream().write(data.toString().getBytes());
            response = new Response(connection.getResponseCode(), connection.getResponseMessage());
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response;
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
    @Nullable
    public static Response putJson(@NotNull String userAgent, @NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
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
            response = new Response(connection.getResponseCode(), connection.getResponseMessage());
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response;
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
    @Nullable
    public static Response delete(@NotNull String userAgent, @NotNull String urlString, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            response = new Response(connection.getResponseCode(), connection.getResponseMessage());
        } catch (final Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response;
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
         * Constructs a new {@link Response} instance
         *
         * @param   code    {@link #code}
         * @param   message {@link #message}
         */
        public Response(int code, @Nullable String message) {
            this.code = code;
            this.message = message;
        }
    }
}
