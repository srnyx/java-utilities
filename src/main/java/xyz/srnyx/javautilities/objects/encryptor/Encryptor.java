package xyz.srnyx.javautilities.objects.encryptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.javautilities.manipulation.Mapper;
import xyz.srnyx.javautilities.objects.encryptor.exceptions.TokenExpiredException;
import xyz.srnyx.javautilities.objects.encryptor.exceptions.TokenInvalidException;
import xyz.srnyx.javautilities.objects.encryptor.exceptions.TokenTamperedException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;


/**
 * Utility class for encrypting and decrypting values using HMAC signatures
 */
public class Encryptor {
    /**
     * The algorithm used for signing
     */
    @NotNull private final String algorithm;
    /**
     * The secret key used for signing
     */
    @NotNull private final SecretKeySpec secret;
    /**
     * The maximum age of a token in milliseconds
     * <br>It's recommended to have a max age to prevent replay attacks
     */
    @Nullable private final Duration maxAge;

    /**
     * Creates a new {@link Encryptor}
     *
     * @param   algorithm                   {@link #algorithm}
     * @param   secret                      {@link #secret}
     * @param   maxAge                      {@link #maxAge}
     *
     * @throws  NoSuchAlgorithmException    if the specified algorithm is not available
     * @throws  InvalidKeyException         if the provided secret is invalid
     */
    public Encryptor(@NotNull String algorithm, @NotNull byte[] secret, @Nullable Duration maxAge) throws NoSuchAlgorithmException, InvalidKeyException {
        this.algorithm = algorithm;
        this.secret = new SecretKeySpec(secret, algorithm);
        this.maxAge = maxAge;

        // Test Mac instance for validity
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(this.secret);
    }

    /**
     * Generates a signature for the given payload
     *
     * @param   payload the payload to sign
     *
     * @return          the signature, or {@code null} if an error occurred
     */
    @Nullable
    private byte[] getSignature(@NotNull String payload) {
        try {
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(secret);
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (final Exception e) {
            // This should never happen since we test the Mac instance in the constructor
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts a value by creating a signed token that includes the value and a timestamp
     * <br>The token format is {@code base64(value:timestamp:signature)}
     *
     * @param   value   the value to encrypt, will be converted to string using {@link Object#toString()}
     *
     * @return          the encrypted token, or {@code null} if an error occurred during signature generation
     */
    @Nullable
    public String encrypt(@NotNull Object value) {
        final String payload = value + ":" + System.currentTimeMillis();
        final byte[] signature = getSignature(payload);
        if (signature == null) return null; // Should never happen
        final String token = payload + ":" + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypts a token by verifying its signature and timestamp
     *
     * @param   token                   the token to decrypt
     *
     * @return                          the original value if the token is valid and not expired, otherwise {@code null}
     *
     * @throws  TokenInvalidException   if the token is invalid
     * @throws  TokenExpiredException   if the token has expired
     * @throws  TokenTamperedException  if the token has been tampered with
     */
    @Nullable
    public String decrypt(@NotNull String token) throws TokenInvalidException, TokenExpiredException, TokenTamperedException {
        // Decode token
        final String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        final String[] parts = decoded.split(":");
        if (parts.length != 3) throw new TokenInvalidException("Token does not have 3 parts");

        // Get casted value and timestamp
        final String value = parts[0];
        if (value == null) throw new TokenInvalidException("Value is null");
        final Optional<Long> timestamp = Mapper.toLong(parts[1]);
        if (!timestamp.isPresent()) throw new TokenInvalidException("Timestamp is not a valid long");

        // Check age
        if (maxAge != null && System.currentTimeMillis() - timestamp.get() > maxAge.toMillis()) throw new TokenExpiredException();

        // Recompute signature
        final String payload = parts[0] + ":" + parts[1];
        final byte[] expectedSig = getSignature(payload);
        if (expectedSig == null) return null; // Should never happen
        final byte[] actualSig = Base64.getUrlDecoder().decode(parts[2]);
        if (!Arrays.equals(expectedSig, actualSig)) throw new TokenTamperedException();

        return value;
    }
}
