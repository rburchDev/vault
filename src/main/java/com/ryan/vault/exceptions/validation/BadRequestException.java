package com.ryan.vault.exceptions.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {
    public BadRequestException(String message) {super(message);}
    public BadRequestException(Throwable cause) {initCause(cause);}

    public BadRequestException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
