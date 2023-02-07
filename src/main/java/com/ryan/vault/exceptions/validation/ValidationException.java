package com.ryan.vault.exceptions.validation;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Document does not exist")
public class ValidationException extends Exception{
    public ValidationException(String message) {super(message);}
    public ValidationException(Throwable cause) {initCause(cause);}

    public ValidationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
