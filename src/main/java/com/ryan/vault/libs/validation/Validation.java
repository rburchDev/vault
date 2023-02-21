package com.ryan.vault.libs.validation;

import com.mongodb.client.result.DeleteResult;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.exceptions.validation.NotFoundException;

import org.bson.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validation extends Base {
    public Document checkResponse(String response) throws NotFoundException {
        LOGGER.info("Checking Response from Mongo");
        Document docResponse = new Document();

        if (response.equals("Success")) {
            docResponse = docResponse.append("Response", "Success");
        } else {
            docResponse = docResponse.append("Response", "Failed");
        }
        return docResponse;
    }

    public Document checkResponse(Document response) throws NotFoundException {
        LOGGER.info("Checking Response from Mongo");

        if (response != null) {
            LOGGER.info("Response was not NULL");
            return response;
        } else {
            LOGGER.warn("Response was NULL");
            throw new NotFoundException("Validation return a NULL response");
        }
    }

    public String checkResponse(DeleteResult response) throws NotFoundException {
        LOGGER.info("Checking response from Mongo for DELETE");

        if (response.wasAcknowledged()) {
            LOGGER.info("Success with DELETE");
            return "Success";
        } else {
            LOGGER.info("Failure with DELETE");
            return "Failure";
        }
    }

    public boolean checkSiteString(String site) {
        //Check that the Site string does not contain special characters
        Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(site);

        return matcher.find();
    }
}
