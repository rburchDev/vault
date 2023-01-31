package com.ryan.vault.exceptions.mongo;

public class MongoDbException extends Exception {

    public MongoDbException(Throwable cause) { initCause(cause);}

    public MongoDbException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
