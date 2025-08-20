package xyz.srnyx.javautilities.objects.encryptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.javautilities.manipulation.Mapper;
import xyz.srnyx.javautilities.objects.encryptor.exceptions.TokenExpiredException;
import xyz.srnyx.javautilities.objects.encryptor.exceptions.TokenInvalidException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;


/**
 * Utility class for encrypting and decrypting values using HMAC signatures
 */
public class Encryptor {
    /**
     * The algorithm used for encryption
     * <br>AES (Advanced Encryption Standard) is a symmetric encryption algorithm widely used across the globe
     * <br>It is known for its speed and security, making it suitable for encrypting sensitive data
     */
    @NotNull private static final String ALGORITHM = "AES";
    /**
     * The transformation string for AES in GCM mode with NoPadding
     * <br>GCM (Galois/Counter Mode) is a mode of operation for symmetric key cryptographic block ciphers
     * <br>It provides both confidentiality and data integrity
     */
    @NotNull private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    /**
     * The size of the initialization vector (IV) in bits
     * <br>GCM typically uses a 96-bit IV, which is recommended for security
     * <br>Using a unique IV for each encryption operation is crucial to prevent replay attacks
     */
    private static final int IV_SIZE = 12; // 96 bits for GCM
    /**
     * The size of the authentication tag in bits
     * <br>GCM uses a 128-bit tag for authentication, which is standard and provides a good balance between security and performance
     * <br>The tag is used to verify the integrity of the encrypted data
     */
    private static final int TAG_SIZE = 128;

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
     * @param   secret                      {@link #secret}
     * @param   maxAge                      {@link #maxAge}
     *
     * @throws  NoSuchAlgorithmException    if the specified algorithm is not available
     * @throws  InvalidKeyException         if the provided secret is invalid
     * @throws  NoSuchPaddingException      if the specified padding scheme is not available
     */
    public Encryptor(@NotNull byte[] secret, @Nullable Duration maxAge) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        this.secret = new SecretKeySpec(secret, ALGORITHM);
        this.maxAge = maxAge;

        // Validate
        if (maxAge != null && maxAge.isNegative()) throw new IllegalArgumentException("maxAge cannot be negative");
        Cipher.getInstance(TRANSFORMATION).init(Cipher.ENCRYPT_MODE, this.secret, new GCMParameterSpec(TAG_SIZE, new byte[IV_SIZE]));
    }

    /**
     * Generates the cipher text for encryption or decryption
     *
     * @param   mode    the mode of operation ({@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE})
     * @param   iv      the initialization vector (IV) used for GCM mode
     * @param   token   the token to encrypt or decrypt
     *
     * @return          the resulting cipher text as a byte array, or null if an error occurs
     */
    @Nullable
    private byte[] getCipherText(int mode, byte[] iv, byte[] token) {
        try {
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            final GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(mode, secret, spec);
            return cipher.doFinal(token);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts a {@link JsonElement} value and returns a Base64 URL-safe string without padding
     *
     * @param   value   the {@link JsonElement} value to encrypt
     *
     * @return          the encrypted token as a Base64 URL-safe string without padding, or null if encryption fails
     */
    @Nullable
    public String encrypt(@NotNull JsonElement value) {
        // Validate value
        if (value.isJsonNull()) return null;

        // Generate random IV
        final byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);

        // Create payload with timestamp and value
        final JsonObject payload = new JsonObject();
        payload.addProperty("timestamp", String.valueOf(System.currentTimeMillis())); // Store as string to prevent rounding
        payload.add("value", value);

        // Encrypt payload
        final byte[] ciphertext = getCipherText(Cipher.ENCRYPT_MODE, iv, payload.toString().getBytes(StandardCharsets.UTF_8));
        if (ciphertext == null) return null;

        // Combine IV and ciphertext
        final ByteBuffer buffer = ByteBuffer.allocate(IV_SIZE + ciphertext.length);
        buffer.put(iv);
        buffer.put(ciphertext);

        // Encode to Base64 URL-safe string without padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    /**
     * Decrypts a Base64 URL-safe string token and returns the original {@link JsonElement} value
     *
     * @param   token   the Base64 URL-safe string token to decrypt
     *
     * @return          the decrypted {@link JsonElement} value, or null if decryption fails or token is invalid
     */
    @Nullable
    public JsonElement decrypt(@NotNull String token) throws TokenExpiredException, TokenInvalidException {
        // Validate token format
        if (token.isEmpty()) throw new TokenInvalidException("Token is empty or invalid");

        // Decode Base64 URL-safe string
        final byte[] decoded = Base64.getUrlDecoder().decode(token);
        final ByteBuffer buffer = ByteBuffer.wrap(decoded);

        // Get IV from beginning of buffer
        final byte[] iv = new byte[IV_SIZE];
        buffer.get(iv);
        if (buffer.remaining() < 1) throw new TokenInvalidException("Token is invalid or tampered with, no cipherText found");

        // Extract cipherText
        final byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        // Decrypt cipherText
        final byte[] decrypted = getCipherText(Cipher.DECRYPT_MODE, iv, cipherText);
        if (decrypted == null) throw new TokenInvalidException("Decryption failed, token is invalid or tampered with");
        final String decryptedString = new String(decrypted, StandardCharsets.UTF_8);

        // Parse JSON
        final JsonObject jsonObject = Mapper.toJson(decryptedString)
                .flatMap(element -> Mapper.convertJsonElement(element, JsonObject.class))
                .orElseThrow(() -> new TokenInvalidException("Decrypted token is not a valid JSON object"));
        if (!jsonObject.has("timestamp") || !jsonObject.has("value")) throw new TokenInvalidException("Decrypted token is missing required fields");

        // Check timestamp expiration
        final String timestamp = Mapper.convertJsonElement(jsonObject.get("timestamp"), JsonPrimitive.class)
                .flatMap(primitive -> Mapper.convertJsonPrimitive(primitive, String.class))
                .orElseThrow(() -> new TokenInvalidException("Timestamp is not a valid string"));
        final Long timestampValue = Mapper.toLong(timestamp).orElseThrow(() -> new TokenInvalidException("Timestamp is not a valid long value"));
        if (maxAge != null && System.currentTimeMillis() - timestampValue > maxAge.toMillis()) throw new TokenExpiredException();

        // Return value
        final JsonElement value = jsonObject.get("value");
        return value.isJsonNull() ? null : value;
    }
}
