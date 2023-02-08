package com.ryan.vault.api.api_controller;

import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.api.ApiException;
import com.ryan.vault.exceptions.validation.ValidationException;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;
import com.ryan.vault.libs.validation.Validation;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.bson.Document;

import java.util.List;

@RestController
public class ApiController extends Base {

    public ApiController() {
        this.validate = new Validation();
        this.mongo = new Mongo();
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "HealthCheck", description = "Used to determine the health of the application")
    public ResponseEntity<Document> getHealth() {
        Document doc = new Document().append("ping", "pong");

        LOGGER.info("Health Check Success");

        return new ResponseEntity<>(doc, HttpStatus.OK);
    }

    @GetMapping(value = "/getCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> getCreds(@RequestParam(value = "site") String site) throws ApiException {
        try {
            LOGGER.info("Getting Creds for: " + site);
            Document docResponse = this.mongo.getOne("Site", site);

            return new ResponseEntity<>(docResponse, HttpStatus.OK);

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
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<List<Document>> getAllCreds() throws ApiException {
        try {
            LOGGER.info("Getting all stored Creds");
            List<Document> docResponse = this.mongo.getAll();

            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
            throw new ApiException("Decryption Error with API Call", e);
        }
    }

    @PostMapping(value = "/setCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> setCreds(@RequestParam String site,
                                             @RequestParam String username,
                                             @RequestParam String password
    ) throws ApiException {
        try {
            LOGGER.info("Setting Creds for: " + site);
            //upsert is true because we are setting the document
            String response = this.mongo.setOne(site, username, password, true);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Encryption");
            throw new ApiException("Encryption Error with API Call", e);
        } catch (ValidationException e) {
            LOGGER.error("Document returned with a failure when attempting to set");
            throw new ApiException("Validation Error with API Call", e);
        }
    }

    @PatchMapping(value = "/updateCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> updateCreds(@RequestParam String site,
                                                @RequestParam String username,
                                                @RequestParam String password
    ) throws ApiException {
        try{
            LOGGER.info("Updating Creds for: " + site);
            // If method runs without error, continue
            this.mongo.getOne("Site", site);
            //upsert is false because we are updating the document
            String response = this.mongo.setOne(site, username, password, false);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (ValidationException e) {
            LOGGER.error("Document does not exist, please use POST to add document");
            throw new ApiException("Validation Error with API Call", e);
        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
            throw new ApiException("Decryption Error with API Call", e);
        }
    }

    @DeleteMapping(value = "/deleteCredential", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> deleteCreds(@RequestParam String site) throws ApiException {
        try {
            LOGGER.info("Attempting to remove: " + site);
            // If method runs without error, continue
            this.mongo.getOne("Site", site);

            String response = this.mongo.deleteOne(site);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (ValidationException e) {
            LOGGER.error("Document does not exist, please use POST to add document");
            throw new ApiException("Validation Error with API Call", e);
        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
            throw new ApiException("MongoDB Error with API Call", e);
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
            throw new ApiException("Decryption Error with API Call", e);
        }
    }
}
