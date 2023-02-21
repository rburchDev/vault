package com.ryan.vault.exceptions.mongo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MongoDbException extends Exception {
    public MongoDbException(String message) {super(message);}
    public MongoDbException(Throwable cause) { initCause(cause);}

    public MongoDbException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
