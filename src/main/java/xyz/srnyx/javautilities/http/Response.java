package xyz.srnyx.javautilities.http;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.javautilities.manipulation.Mapper;

import java.util.Optional;


/**
 * Represents the response from an HTTP request
 */
public class Response {
    /**
     * The HTTP response code
     * <br>{@code null} = request failed
     */
    @Nullable public final Integer code;
    /**
     * The HTTP response message
     */
    @Nullable public final String message;
    /**
     * The raw HTTP response body
     */
    @Nullable public final String body;

    /**
     * Constructs a new {@link Response} instance
     *
     * @param code    {@link #code}
     * @param message {@link #message}
     * @param body    {@link #body}
     */
    public Response(@Nullable Integer code, @Nullable String message, @Nullable String body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public Response() {
        this(null, null, null);
    }

    public boolean requestFailed() {
        return code == null;
    }

    @NotNull
    public Optional<Integer> getCode() {
        return Optional.ofNullable(code);
    }

    public boolean isCodeFailure() {
        return code == null || (code >= 400 && code < 600);
    }

    @NotNull
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @NotNull
    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    @NotNull
    public Optional<JsonElement> getBodyAsJson() {
        return getBody().flatMap(Mapper::toJson);
    }
}
