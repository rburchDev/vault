package com.ryan.vault.libs.validation;

import com.ryan.vault.libs.base.Base;
import com.ryan.vault.exceptions.validation.ValidationException;

import org.bson.Document;


public class Validation extends Base {
    public Document checkResponse(Document response) throws ValidationException {
        LOGGER.info("Checking Response from Mongo");

        if (response != null) {
            LOGGER.info("Response was not NULL");
            return response;
        } else {
            LOGGER.warn("Response was NULL");
            throw new ValidationException("Validation return a NULL response");
        }
    }
}
