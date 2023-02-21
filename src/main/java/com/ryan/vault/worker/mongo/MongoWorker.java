package com.ryan.vault.worker.mongo;

import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.validation.NotFoundException;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;
import com.ryan.vault.services.email.EmailServiceImpl;
import com.ryan.vault.libs.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Public class to create a MongoWorker to check on pwd length
 */
@Component
public class MongoWorker extends Base {
    @Autowired private EmailServiceImpl email;
    @Value("${spring.mail.username}") private String sender;

    public MongoWorker() {
        this.validate = new Validation();
        this.mongo = new Mongo();
    }

    /**
     * helper function to get the date minus three months
     * @return Date in LocalDate format
     */
    private LocalDate getOldDate() {
        LocalDate currentDate = LocalDate.now();

        return currentDate.minusMonths(3);
    }

    /**
     * Worker function that runs daily at noon to check how old passwords are
     */
    @Scheduled(cron = "@midnight", zone = "MST")
    public void trackPasswordChange() {
        LOGGER.debug("STARTING PASSWORD AGE CHECK");
        Document result;
        try {
            ArrayList<String> updateList = new ArrayList<>();
            LOGGER.info("Running password check");
            //Get date minus three months
            LocalDate minusThreeMonths = getOldDate();

            //Get full list of documents in collection
            List<Document> docResponse = this.mongo.getAll();

            for (Document doc : docResponse) {
                // Get date and convert to date Object
                String date = (String) doc.get("DateSet");
                LocalDate dateObject = LocalDate.parse(date);
                //Check if date set is older than 3 months
                if (dateObject.isBefore(minusThreeMonths)) {
                    String site = (String) doc.get("Site");
                    String logMessage = String.format("%s needs to have its password updated!!!!", site);
                    LOGGER.warn(logMessage);
                    updateList.add(site);
                }
            }

            if (updateList.size() != 0) {
                String body = String.format("Passwords for these sites need to be updated: %s", updateList);
                String response = email.sendEmail(sender, "Password Update", body);
                result = this.validate.checkResponse(response);
                LOGGER.info(result.toString());
            }

        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
        } catch (NotFoundException e) {
            LOGGER.error("Error with validation");
        }
    }
}
