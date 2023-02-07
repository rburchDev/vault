package com.ryan.vault.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Issue with API")
public class ApiException extends Exception {
    public ApiException(Throwable cause) {initCause(cause);}

    public ApiException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
