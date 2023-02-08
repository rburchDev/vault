package com.ryan.vault.worker.mongo;

import com.ryan.vault.exceptions.cryptography.CryptographyException;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.validation.ValidationException;
import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;
import com.ryan.vault.services.email.UpdateEmail;
import com.ryan.vault.libs.validation.Validation;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired private UpdateEmail email;

    public MongoWorker() {
        this.validate = new Validation();
        this.mongo = new Mongo();
    }

    /**
     * helper function to get the current date
     * @return Current date in LocalDate format
     */
    private LocalDate getCurrentDate() {
        LocalDate currentDate = LocalDate.now();

        return currentDate.minusMonths(3);
    }

    /**
     * Worker function that runs daily at midnight to check how old passwords are
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "MST")
    public void trackPasswordChange() {
        Document result;
        try {
            ArrayList<String> updateList = new ArrayList<>();
            LOGGER.info("Running password check");
            LocalDate minusSixMonths = getCurrentDate();

            //Get full list of documents in collection
            List<Document> docResponse = this.mongo.getAll();

            for (Document doc : docResponse) {
                // Get date and convert to date Object
                String date = (String) doc.get("DateSet");
                LocalDate dateObject = LocalDate.parse(date);
                if (dateObject.isBefore(minusSixMonths)) {
                    String site = (String) doc.get("Site");
                    String logMessage = String.format("%s needs to have its password updated!!!!", site);
                    LOGGER.warn(logMessage);
                    updateList.add(site);
                }
            }

            if (updateList.size() != 0) {
                String response = email.sendEmail("rtburch@outlook.com", "Password Update", updateList.toString());
                result = this.validate.checkResponse(response);
                LOGGER.info(result.toString());
            }

        } catch (MongoDbException e) {
            LOGGER.error("Error with MongoDB");
        } catch (CryptographyException e) {
            LOGGER.error("Error with Decryption");
        } catch (ValidationException e) {
            LOGGER.error("Error with validation");
        }
    }
}
