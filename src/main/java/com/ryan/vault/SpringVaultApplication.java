package com.ryan.vault;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories
@SecurityScheme(name = "vaultAPI", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SpringVaultApplication {
    private static final Logger LOGGER = LogManager.getLogger(SpringVaultApplication.class);
    public static void main(String[] args) {

        LOGGER.info("Starting SpringBoot Vault APP");

        SpringApplication.run(SpringVaultApplication.class, args);

    }

}
