package com.ryan.vault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SpringVaultApplication {
    private static final Logger LOGGER = LogManager.getLogger(SpringVaultApplication.class);
    public static void main(String[] args) {

        LOGGER.info("Starting SpringBoot Vault APP");

        SpringApplication.run(SpringVaultApplication.class, args);

    }

}
