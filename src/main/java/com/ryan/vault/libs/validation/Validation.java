package com.ryan.vault.libs.validation;

import com.mongodb.client.result.DeleteResult;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.exceptions.validation.ValidationException;

import org.bson.Document;


public class Validation extends Base {
    public Document checkResponse(String response) throws ValidationException {
        LOGGER.info("Checking Response from Mongo");
        Document docResponse = new Document();

        if (response.equals("Success")) {
            docResponse = docResponse.append("Response", "Success");
        } else {
            docResponse = docResponse.append("Response", "Failed");
        }
        return docResponse;
    }

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

    public String checkResponse(DeleteResult response) throws ValidationException {
        LOGGER.info("Checking response from Mongo for DELETE");

        if (response.wasAcknowledged()) {
            LOGGER.info("Success with DELETE");
            return "Success";
        } else {
            LOGGER.info("Failure with DELETE");
            return "Failure";
        }
    }
}
