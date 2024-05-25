package xyz.srnyx.javautilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Utility class for making HTTP requests
 */
public class HttpUtility {
    /**
     * Sends a GET request to the specified URL and returns the result of the specified function
     *
     * @param   userAgent   the user agent to use
     * @param   url         the URL to request from
     * @param   function    the function to apply to the {@link InputStreamReader}
     *
     * @param   <T>         the type of the result of the specified function
     *
     * @return              the result of the specified function, or null if the request failed
     */
    @Nullable
    public static <T> T get(@NotNull String userAgent, @NotNull String url, Function<InputStreamReader, T> function) {
        T result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connection.getResponseCode() == 404) return null;
            result = function.apply(new InputStreamReader(connection.getInputStream()));
        } catch (final IOException ignored) {
            // Ignored
        }
        if (connection != null) connection.disconnect();
        return result;
    }

    /**
     * Sends a GET request to the specified URL and returns the result as a {@link String}
     *
     * @param   userAgent   the user agent to use
     * @param   urlString   the URL to request from
     *
     * @return              the {@link String}, or null if the request failed
     */
    @Nullable
    public static String getString(@NotNull String userAgent, @NotNull String urlString) {
        return get(userAgent, urlString, reader -> new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
    }

    /**
     * Sends a GET request to the specified URL and returns the result as a {@link JsonElement}
     *
     * @param   userAgent   the user agent to use when retrieving the {@link JsonElement}
     * @param   urlString   the URL to retrieve the {@link JsonElement} from
     *
     * @return              the {@link JsonElement} retrieved from the specified URL
     */
    @Nullable
    public static JsonElement getJson(@NotNull String userAgent, @NotNull String urlString) {
        return get(userAgent, urlString, reader -> new JsonParser().parse(reader));
    }

    /**
     * Sends a POST request to the specified URL with the specified {@link JsonObject JSON data}
     *
     * @param   userAgent   the user agent to use
     * @param   urlString   the URL to send the POST request to
     * @param   data        the {@link JsonObject JSON data} to send with the POST request
     *
     * @return              the response code of the request
     */
    public static int postJson(@NotNull String userAgent, @NotNull String urlString, @NotNull JsonElement data) {
        int responseCode = -1;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(data.toString().getBytes());
            responseCode = connection.getResponseCode();
        } catch (final IOException ignored) {
            // Ignored
        }
        if (connection != null) connection.disconnect();
        return responseCode;
    }

    /**
     * Sends a PUT request to the specified URL with the specified {@link JsonElement JSON data}
     *
     * @param   userAgent   the user agent to use
     * @param   urlString   the URL to send the PUT request to
     * @param   data        the {@link JsonElement JSON data} to send with the PUT request
     *
     * @return              the response code of the request
     */
    public static int putJson(@NotNull String userAgent, @NotNull String urlString, @NotNull JsonElement data) {
        int responseCode = -1;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(data.toString().getBytes());
            responseCode = connection.getResponseCode();
        } catch (final IOException ignored) {
            // Ignored
        }
        if (connection != null) connection.disconnect();
        return responseCode;
    }

    /**
     * Sends a DELETE request to the specified URL
     *
     * @param   userAgent   the user agent to use
     * @param   urlString   the URL to send the DELETE request to
     *
     * @return              the response code of the request
     */
    public static int delete(@NotNull String userAgent, @NotNull String urlString) {
        int responseCode = -1;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("User-Agent", userAgent);
            responseCode = connection.getResponseCode();
        } catch (final IOException ignored) {
            // Ignored
        }
        if (connection != null) connection.disconnect();
        return responseCode;
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
