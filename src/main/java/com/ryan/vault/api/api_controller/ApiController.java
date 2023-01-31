package com.ryan.vault.api.api_controller;

import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.api.ApiException;
import com.ryan.vault.exceptions.validation.ValidationException;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.bson.Document;

import java.util.List;

@RestController
public class ApiController extends Base {

    public ApiController() {}

    @GetMapping(value = "/getCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Document> getCreds(@RequestParam(value = "site") String site) throws ApiException {
        try {
            LOGGER.info("Getting Creds for: " + site);
            Document docResponse = Mongo.getOne("Site", site);

            return new ResponseEntity<>(docResponse, HttpStatus.ACCEPTED);
        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
            throw new ApiException("Decryption Error with API Call", e);
        } catch (ValidationException e) {
            LOGGER.error("Error with validating MongoDB response from GET call");
            throw new ApiException("Validation Error with API Call", e);
        }
    }

    @GetMapping(value = "/getAllCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Document>> getAllCreds() throws ApiException {
        try {
            LOGGER.info("Getting all stored Creds");
            List<Document> docResponse = Mongo.getAll();

            return new ResponseEntity<>(docResponse, HttpStatus.ACCEPTED);
        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
            throw new ApiException("Encryption Error with API Call", e);
        }
    }

    @PostMapping(value = "/setCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Document> setCreds(@RequestParam String site,
                                             @RequestParam String username,
                                             @RequestParam String password
    ) throws ApiException {
        try {
            LOGGER.info("Setting Creds for: " + site);
            String response = Mongo.setOne(site, username, password);
            Document docResponse = new Document();
            if (response.equals("Success")) {
                docResponse = docResponse.append("Response", "Success");
            } else {
                docResponse = docResponse.append("Response", "Failed");
            }
            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.ACCEPTED);
        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Encryption");
            throw new ApiException("Encryption Error with API Call", e);
        }
    }
}
