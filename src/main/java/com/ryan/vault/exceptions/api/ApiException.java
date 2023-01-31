package com.ryan.vault.exceptions.api;

public class ApiException extends Exception {
    public ApiException(Throwable cause) {initCause(cause);}

    public ApiException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
