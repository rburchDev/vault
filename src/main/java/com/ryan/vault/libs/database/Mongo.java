package com.ryan.vault.libs.database;

import com.mongodb.client.*;
import com.ryan.vault.libs.cryptography.Encrypt;
import com.ryan.vault.libs.cryptography.Decrypt;
import com.ryan.vault.libs.utility.Utility;
import com.ryan.vault.libs.validation.Validation;
import com.ryan.vault.models.DocumentModel;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.validation.ValidationException;
import com.ryan.vault.exceptions.mongo.MongoDbException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

public class Mongo extends Base {
    private static final Dotenv DOTENV = Utility.getDotEnv(false);
    private static final String MONGOCLIENT = Objects.requireNonNull(DOTENV.get("MONGOURL"));
    private static final String SECRET = Objects.requireNonNull(DOTENV.get("SECRET"));
    private static final String DATABASE = "vault";
    private static final String COLLECTION = "passwordCollection";
    public static Encrypt encrypt = new Encrypt();
    public static Decrypt decrypt = new Decrypt();
    public static Validation validate = new Validation();

    private static Document setDocument(String username, String secret) {
        try {

            DocumentModel documentModel = new DocumentModel(username, secret, LocalDate.now().toString());

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(documentModel);
            String docString = mapper.writeValueAsString(documentModel);

            return Document.parse(docString);
        } catch (JsonProcessingException e) {
            LOGGER.warn(e);
        }
        return null;
    }

    /**
     * helper method to test if the connection to Mongo is good
     * @param mongoConnection the established Mongo connection
     * @return Command result or null
     * @throws MongoDbException Throw exception if Mongo issue occurs
     */
    private static Document testConnection(MongoClient mongoConnection) throws MongoDbException {

        MongoDatabase db = mongoConnection.getDatabase(DATABASE);
        Document commandResult = null;
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            commandResult = db.runCommand(command);
            LOGGER.info("Connection successful to Mongo");
            LOGGER.info(commandResult);
        } catch (MongoException e) {
            LOGGER.error("ERROR WITH MONGO");
            throw new MongoDbException("Error with Mongo", e);

        }
        return commandResult;
    }

    /**
     * method to establish a Mongo connection
     * @return the established Mongo connection
     * @throws MongoDbException Throw exception if Mongo issue occurs
     */
    private static MongoCollection<Document> mongoClient() throws MongoDbException {

        MongoClient mongoConnection = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(MONGOCLIENT))
                        .build()
        );
        MongoDatabase db = mongoConnection.getDatabase(DATABASE);

        MongoCollection<Document> collection = db.getCollection(COLLECTION);
            try {
                Document response = testConnection(mongoConnection);
                LOGGER.info("Connection Test result: " + response);
            } catch (MongoException e) {
                LOGGER.error("ERROR with Mongo Connection");
                throw new MongoDbException("Error with Mongo Connection", e);
            }
            return collection;

    }

    /**
     * method to insert or update a document in the vault collection
     * @param site The site for the credentials to be saved
     * @param username The username for the site
     * @param password The password for the site
     * @return String Success or Failed
     * @throws MongoDbException Throw exception if Mongo issue occurs
     * @throws CryptographyException Throw exception if encrypting issue occurs
     */
    public static String setOne(String site, String username, String password) throws MongoDbException, CryptographyException {
        try {

            String secretKey = encrypt.crypt(password, SECRET);

            Document modeledDoc = Objects.requireNonNull(setDocument(username, secretKey));

            MongoCollection<Document> collection = mongoClient();

            Bson filter = Filters.eq("Site", site);
            Bson update = new Document("$set", modeledDoc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            collection.updateOne(filter, update, options);

        } catch (MongoException | MongoDbException e) {
            LOGGER.error("An error occurred with MongoDB");
            throw new MongoDbException("Error with setting Mongo Document", e);
        } catch (RuntimeException er) {
            LOGGER.error("A Runtime Error occurred");
            throw new MongoDbException("Error with setting Mongo Document", er);
        } catch (CryptographyException e) {
            LOGGER.error("An error occurred with Encrypting the password");
            throw new CryptographyException("Error with Encrypting", e);
        }
        return "Success";
    }

    /**
     * method to find the passed in document based on the key value pair
     * @param key the key to look for
     * @param value the value to look for
     * @return Found Document or null
     * @throws MongoDbException Throw exception if Mongo issue occurs
     * @throws CryptographyException Throw exception if decrypting issue occurs
     */
    public static Document getOne(String key, String value)
            throws MongoDbException, CryptographyException, ValidationException {
        Document response;
        Object docPassword;
        try {

            MongoCollection<Document> collection = mongoClient();

            response = collection.find(eq(key, value))
                    .first();
            // Check if the response is null or not, if null throw ERROR
            response = validate.checkResponse(response);

            docPassword = response.get("Password");
            String password = decrypt.crypt(docPassword.toString(), SECRET);
            response.append("Password", password);

        } catch (MongoException | MongoDbException e) {
            LOGGER.error("An error occurred with MongoDB");
            throw new MongoDbException("Error with getting Mongo Document", e);
        } catch (RuntimeException er) {
            LOGGER.error("A Runtime Error occurred");
            throw new MongoDbException("Error with getting Mongo Document", er);
        } catch (CryptographyException e) {
            LOGGER.error("An error occurred with Decrypting the password");
            throw new CryptographyException("Error with Decrypting", e);
        } catch (ValidationException e) {
            LOGGER.warn("The response returned a Null when attempting to get the document");
            throw new ValidationException("Response returned as NULL", e);
        }
        return response;
    }

    public static List<Document> getAll()
            throws MongoDbException, CryptographyException {
        List<Document> response = new ArrayList<>();
        Object docPassword;
        Document cursorObject;
        try {
            MongoCollection<Document> collection = mongoClient();
            // get the full collection as an iterator
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                // Loop through each document and add to our List which we will return
                while (cursor.hasNext()) {
                    cursorObject = cursor.next();
                    // Get the encrypted password and decrypt and update Document
                    docPassword = cursorObject.get("Password");
                    String password = decrypt.crypt(docPassword.toString(), SECRET);
                    cursorObject.append("Password", password);

                    response.add(cursorObject);
                }
            }

            System.out.println(response);
        } catch (MongoException | MongoDbException e) {
            LOGGER.error("An error occurred with MongoDB");
            throw new MongoDbException("Error with getting Mongo Document", e);
        } catch (RuntimeException er) {
            LOGGER.error("A Runtime Error occurred");
            throw new MongoDbException("Error with getting Mongo Document", er);
        } catch (CryptographyException e) {
            LOGGER.error("An error occurred with Decrypting the password");
            throw new CryptographyException("Error with Decrypting", e);
        }
        return response;
    }
}

