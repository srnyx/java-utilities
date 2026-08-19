package xyz.srnyx.javautilities.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.javautilities.parents.Stringable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Http extends Stringable {
    @NotNull private final String userAgent; 
    private final boolean debug;

    private Http(@NotNull String userAgent, boolean debug) {
        this.userAgent = userAgent;
        this.debug = debug;
    }

    @NotNull
    public Builder builder() {
        return new Builder(this);
    }

    @NotNull
    public Response get(@NotNull String url, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        Response result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Accept", "*/*");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            result = new Response(connection.getResponseCode(), connection.getResponseMessage(), getResponseBody(connection));
        } catch (final Exception e) {
            if (debug) e.printStackTrace();
        }

        if (connection != null) connection.disconnect();
        return result != null ? result : new Response();
    }

    /**
     * Sends a POST request to the specified URL with the specified data
     *
     * @param   urlString           the URL to send the POST request to
     * @param   data                the data to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public Response post(@NotNull String urlString, byte @Nullable [] data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
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
            if (debug) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response != null ? response : new Response();
    }

    /**
     * Sends a POST request to the specified URL with the specified {@link JsonObject JSON data}
     *
     * @param   urlString           the URL to send the POST request to
     * @param   data                the {@link JsonObject JSON data} to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public Response postJson(@NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        return post(urlString, data != null ? data.toString().getBytes() : null, connection -> {
            connection.setRequestProperty("Content-Type", "application/json");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
        });
    }

    /**
     * Sends a POST request to the specified URL with the specified form data
     *
     * @param   urlString           the URL to send the POST request to
     * @param   formData            the form data to send with the POST request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public Response postFormUrlEncoded(@NotNull String urlString, @NotNull Map<String, String> formData, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        final String formBody = formData.entrySet().stream()
                .map(entry -> {
                    try {
                        return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
                    } catch (final Exception e) {
                        if (debug) e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("&"));
        return post(urlString, formBody.getBytes(), connection -> {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (connectionConsumer != null) connectionConsumer.accept(connection);
        });
    }

    /**
     * Sends a PUT request to the specified URL with the specified {@link JsonElement JSON data}
     *
     * @param   urlString           the URL to send the PUT request to
     * @param   data                the {@link JsonElement JSON data} to send with the PUT request
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public Response putJson(@NotNull String urlString, @Nullable JsonElement data, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
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
            if (debug) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response != null ? response : new Response();
    }

    /**
     * Sends a DELETE request to the specified URL
     *
     * @param   urlString           the URL to send the DELETE request to
     * @param   connectionConsumer  the consumer to apply to the {@link HttpURLConnection}
     *
     * @return                      a {@link Response} object containing the response code and message, or null if the request failed
     */
    @NotNull
    public Response delete(@NotNull String urlString, @Nullable Consumer<HttpURLConnection> connectionConsumer) {
        Response response = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connectionConsumer != null) connectionConsumer.accept(connection);
            response = new Response(connection.getResponseCode(), connection.getResponseMessage(), getResponseBody(connection));
        } catch (final Exception e) {
            if (debug) e.printStackTrace();
        }
        if (connection != null) connection.disconnect();
        return response != null ? response : new Response();
    }

    /**
     * Gets the response body from the specified {@link HttpURLConnection}
     *
     * @param   connection  the {@link HttpURLConnection} to get the response body from
     *
     * @return              the response body, or null if an error occurred
     */
    @Nullable
    private String getResponseBody(@NotNull HttpURLConnection connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final Exception e) {
            if (debug) e.printStackTrace();
        }
        return null;
    }

    public static class Builder extends Stringable {
        @Nullable private String userAgent;
        private boolean debug = false;
        
        public Builder() {}
        
        public Builder(@NotNull Http http) {
            this.userAgent = http.userAgent;
            this.debug = http.debug;
        }

        @NotNull
        public Builder userAgent(@Nullable String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        @NotNull
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        @NotNull
        public Http build() throws IllegalArgumentException {
            if (userAgent == null) throw new IllegalArgumentException("userAgent must be set");

            return new Http(userAgent, debug);
        }
    }
}
