package com.ryan.vault.libs.base;

import com.ryan.vault.libs.validation.Validation;
import com.ryan.vault.libs.database.Mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;

public abstract class Base {
    public static final Logger LOGGER = LogManager.getLogger();
    public static MessageDigest sha;
    public Validation validate;
    public Mongo mongo;

    public Base() {
    }
}
