package com.ryan.vault.libs.database;

import com.ryan.vault.libs.cryptography.Encrypt;
import com.ryan.vault.libs.cryptography.Decrypt;
import com.ryan.vault.libs.utility.Utility;
import com.ryan.vault.libs.validation.Validation;
import com.ryan.vault.models.DocumentModel;
import com.ryan.vault.libs.base.Base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import java.time.LocalDate;

public class Mongo extends Base {
    private static final Dotenv DOTENV = Utility.getDotEnv(false);
    private static final String MONGOCLIENT = DOTENV.get("MONGOURL");
    private static final String SECRET = DOTENV.get("SECRET");
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
     */
    private static Document testConnection(MongoClient mongoConnection) {

        MongoDatabase db = mongoConnection.getDatabase(DATABASE);
        Document commandResult = null;
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            commandResult = db.runCommand(command);
            LOGGER.info("Connection successful to Mongo");
            LOGGER.info(commandResult);
        } catch (MongoException e) {
            LOGGER.warn("ERROR WITH MONGO");
            LOGGER.warn(e);

        }
        return commandResult;
    }

    /**
     * method to establish a Mongo connection
     * @return the established Mongo connection
     * @throws MongoException Throw exception if Mongo issue occurs
     */
    private static MongoCollection<Document> mongoClient() throws MongoException {

        MongoClient mongoConnection = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(MONGOCLIENT))
                        .build()
        );
        MongoDatabase db = mongoConnection.getDatabase(DATABASE);

        MongoCollection<Document> collection = db.getCollection(COLLECTION);
            try {
                Document response = testConnection(mongoConnection);
                System.out.println(response);
            } catch (MongoException e) {
                LOGGER.warn("ERROR with Mongo Connection");
                LOGGER.warn(e);
            }
            return collection;

    }

    /**
     * method to insert or update a document in the vault collection
     * @param site The site for the credentials to be saved
     * @param username The username for the site
     * @param password The password for the site
     * @return String Success or Failed
     * @throws MongoException Throw exception if Mongo issue occurs
     */
    public static String setOne(String site, String username, String password) throws MongoException {
        try {
            System.out.println(password);
            String secretKey = encrypt.crypt(password, SECRET);
            System.out.println(secretKey);
            Document modeledDoc = setDocument(username, secretKey);

            MongoCollection<Document> collection = mongoClient();
            System.out.println(modeledDoc);
            Bson filter = Filters.eq("Site", site);
            Bson update = new Document("$set", modeledDoc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            collection.updateOne(filter, update, options);

        } catch (MongoException e) {
            LOGGER.warn("ERROR WITH MONGO");
            LOGGER.warn(e);
            return "Failed";
        }
        return "Success";
    }

    /**
     * method to find the passed in document based on the key value pair
     * @param key the key to look for
     * @param value the value to look for
     * @return Found Document or null
     * @throws MongoException Throw exception if Mongo issue occurs
     */
    public static Document getOne(String key, String value) throws MongoException {
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

        } catch (MongoException e) {
            LOGGER.warn("ERROR WITH MONGO");
            LOGGER.warn(e);
            return null;
        } catch (RuntimeException er) {
            LOGGER.error(er);
            return null;
        }
        return response;
    }
}

