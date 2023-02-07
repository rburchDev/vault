package com.ryan.vault.exceptions.cryptography;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to perform cryptographic functions")
public class CryptographyException extends Exception {
    public CryptographyException(Throwable cause) {initCause(cause);}

    public CryptographyException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
