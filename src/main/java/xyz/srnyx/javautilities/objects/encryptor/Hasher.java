package xyz.srnyx.javautilities.objects.encryptor;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Hasher {
    @NotNull
    public static String hash(@NotNull String algorithm, @NotNull String string) {
        try {
            // Hash
            final byte[] hash = MessageDigest.getInstance(algorithm).digest(string.getBytes(StandardCharsets.UTF_8));

            // Hash bytes to string hex
            final char[] hexChars = new char[hash.length * 2];
            final char[] hexDigits = "0123456789abcdef".toCharArray();
            for (int i = 0; i < hash.length; i++) {
                final int v = hash[i] & 0xFF;
                hexChars[i * 2] = hexDigits[v >>> 4];
                hexChars[i * 2 + 1] = hexDigits[v & 0x0F];
            }
            return new String(hexChars);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static String hashSHA256(@NotNull String string) {
        return hash("SHA-256", string);
    }
}
