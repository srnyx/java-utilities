package xyz.srnyx.javautilities.objects.encryptor.exceptions;


public class TokenExpiredException extends DecryptionException {
    public TokenExpiredException() {
        super("Token has expired");
    }
}
