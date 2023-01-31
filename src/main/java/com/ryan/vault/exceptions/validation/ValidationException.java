package com.ryan.vault.exceptions.validation;


public class ValidationException extends Exception{
    public ValidationException(String message) {super(message);}
    public ValidationException(Throwable cause) {initCause(cause);}

    public ValidationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
