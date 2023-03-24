package com.ryan.vault.api.api_controller;

import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.validation.NotFoundException;
import com.ryan.vault.exceptions.validation.BadRequestException;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;
import com.ryan.vault.libs.utility.CleanInputs;
import com.ryan.vault.libs.validation.Validation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.bson.Document;

import java.util.List;

@RestController
@SecurityRequirement(name = "vaultAPI")
public class ApiController extends Base {

    public ApiController() {
        this.validate = new Validation();
        this.mongo = new Mongo();
        this.cleanInputs = new CleanInputs();
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
    public ResponseEntity<Document> getCreds(@RequestParam(value = "site") String site)
            throws NotFoundException, MongoDbException, CryptographyException, BadRequestException {
        try {
            LOGGER.info("Getting Creds for: " + site);

            //Validate Site string does not contain special characters
            if (this.validate.checkSiteString(site)) {
                throw new BadRequestException("Item contains Special Characters in the Site name... Please correct");
            }

            //Set site as lowercase and remove white space
            String convertedString = this.cleanInputs.setSiteString(site);

            Document docResponse = this.mongo.getOne("Site", convertedString);

            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (MongoDbException e) {
            LOGGER.error(e.getMessage());
            throw new MongoDbException(e.getMessage());
        } catch (CryptographyException e) {
            LOGGER.error(e.getMessage());
            throw new CryptographyException(e.getMessage());
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/getAllCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<List<Document>> getAllCreds() throws MongoDbException, CryptographyException {
        try {
            LOGGER.info("Getting all stored Creds");
            List<Document> docResponse = this.mongo.getAll();

            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (MongoDbException e) {
            LOGGER.error(e.getMessage());
            throw new MongoDbException(e.getMessage());
        } catch (CryptographyException e) {
            LOGGER.error(e.getMessage());
            throw new CryptographyException(e.getMessage());
        }
    }

    @PostMapping(value = "/setCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> setCreds(@RequestParam String site,
                                             @RequestParam String username,
                                             @RequestParam String password
    ) throws MongoDbException, NotFoundException, CryptographyException, BadRequestException {
        try {
            LOGGER.info("Setting Creds for: " + site);
            //Validate Site string does not contain special characters
            if (this.validate.checkSiteString(site)) {
                throw new BadRequestException("Item contains Special Characters in the Site name... Please correct");
            }

            //Set site as lowercase and remove white space
            String convertedString = this.cleanInputs.setSiteString(site);

            //Need to check if the credentials exists already
            try{
                Document getDocResponse = this.mongo.getOne("Site", convertedString);
                if (!getDocResponse.isEmpty()) {
                    throw new BadRequestException("Item already exists... Please use PUT method to update...");
                }
            } catch (NotFoundException e) {
                LOGGER.debug(e.getMessage());
            }

            //upsert is true because we are setting the document
            String response = this.mongo.setOne(convertedString, username, password, true);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (MongoDbException e) {
            LOGGER.error(e.getMessage());
            throw new MongoDbException(e.getMessage());
        } catch (CryptographyException e) {
            LOGGER.error(e.getMessage());
            throw new CryptographyException(e.getMessage());
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @PatchMapping(value = "/updateCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> updateCreds(@RequestParam String site,
                                                @RequestParam String username,
                                                @RequestParam String password
    ) throws NotFoundException, MongoDbException, CryptographyException, BadRequestException {
        try{
            LOGGER.info("Updating Creds for: " + site);

            //Validate Site string does not contain special characters
            if (this.validate.checkSiteString(site)) {
                throw new BadRequestException("Item contains Special Characters in the Site name... Please correct");
            }

            //Set site as lowercase and remove white space
            String convertedString = this.cleanInputs.setSiteString(site);

            // If method runs without error, continue
            this.mongo.getOne("Site", convertedString);
            //upsert is false because we are updating the document
            String response = this.mongo.setOne(convertedString, username, password, false);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (NotFoundException e) {
            LOGGER.error("Document does not exist, please use POST to add document");
            throw new NotFoundException("Document does not exist, please use POST to add document", e);
        } catch (MongoDbException e) {
            LOGGER.error(e.getMessage());
            throw new MongoDbException(e.getMessage());
        } catch (CryptographyException e) {
            LOGGER.error(e.getMessage());
            throw new CryptographyException(e.getMessage());
        } catch (BadRequestException e) {
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping(value = "/deleteCredential", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Vault", description = "Set of APIs to maintain a collection of credentials in a secure Vault")
    public ResponseEntity<Document> deleteCreds(@RequestParam String site)
            throws MongoDbException, NotFoundException, CryptographyException, BadRequestException {
        try {
            LOGGER.info("Attempting to remove: " + site);

            //Validate Site string does not contain special characters
            if (this.validate.checkSiteString(site)) {
                throw new BadRequestException("Item contains Special Characters in the Site name... Please correct");
            }

            //Set site as lowercase and remove white space
            String convertedString = this.cleanInputs.setSiteString(site);

            // If method runs without error, continue
            this.mongo.getOne("Site", convertedString);

            String response = this.mongo.deleteOne(convertedString);

            //Check response
            Document docResponse = this.validate.checkResponse(response);

            LOGGER.info(docResponse);
            return new ResponseEntity<>(docResponse, HttpStatus.OK);

        } catch (NotFoundException e) {
            LOGGER.error("Document does not exist, please use POST to add document");
            throw new NotFoundException("Document does not exist, please use POST to add document", e);
        } catch (MongoDbException e) {
            LOGGER.error(e.getMessage());
            throw new MongoDbException(e.getMessage());
        } catch (CryptographyException e) {
            LOGGER.error(e.getMessage());
            throw new CryptographyException(e.getMessage());
        } catch (BadRequestException e) {
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }
}
