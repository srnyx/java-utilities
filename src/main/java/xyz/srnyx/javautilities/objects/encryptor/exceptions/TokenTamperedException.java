package xyz.srnyx.javautilities.objects.encryptor.exceptions;


public class TokenTamperedException extends DecryptionException {
    public TokenTamperedException() {
        super("Token has been tampered with");
    }
}
