package com.ryan.vault.libs.validation;

import com.ryan.vault.libs.base.Base;

import org.bson.Document;

import java.lang.RuntimeException;

public class Validation extends Base {
    public Document checkResponse(Document response) throws RuntimeException {
        LOGGER.info("Checking Response from Mongo");

        if (response != null) {
            LOGGER.info("Response was not NULL");
            return response;
        } else {
            LOGGER.warn("Response was NULL");
            throw new RuntimeException("Response was Null");
        }
    }
}
