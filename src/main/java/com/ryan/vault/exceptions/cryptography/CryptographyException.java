package com.ryan.vault.exceptions.cryptography;

public class CryptographyException extends Exception {
    public CryptographyException(Throwable cause) {initCause(cause);}

    public CryptographyException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
