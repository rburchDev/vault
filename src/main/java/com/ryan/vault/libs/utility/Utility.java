package com.ryan.vault.libs.utility;

import com.ryan.vault.libs.base.Base;

import io.github.cdimascio.dotenv.Dotenv;

/**
 *Class to build the Utility for setting the environment variable
 */

public class Utility extends Base {

    private static final String LOCAL_ENV_FILENAME = "vault-var.env";
    private static final String TEST_ENV_FILENAME = "vault-test-var.env";
    private static final String DIRECTORY = "src/main/resources/env-files";

    /**
     * Method to get the environment file and set the variables
     * @param test If set to true, set the test environment file
     * @return Dotenv instance
     */

    public static Dotenv getDotEnv(boolean test) {
        Dotenv hold;
        LOGGER.info("Starting ENV Utility");
        //Check if test is set to true
        if (test) {
            LOGGER.info("Starting for TEST");
            hold = Dotenv.configure()
                    .directory(DIRECTORY)
                    .filename(TEST_ENV_FILENAME)
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
        } else {
            LOGGER.info("Starting for MAIN");
            hold = Dotenv.configure()
                    .directory(DIRECTORY)
                    .filename(LOCAL_ENV_FILENAME)
                    .ignoreIfMissing()
                    .ignoreIfMalformed()
                    .load();
        }
        LOGGER.info("ENV Utility Set");
        return hold;
    }
}
