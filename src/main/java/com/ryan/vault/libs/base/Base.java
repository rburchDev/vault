package com.ryan.vault.libs.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;

public abstract class Base {
    public static final Logger LOGGER = LogManager.getLogger();
    public static MessageDigest sha;
    public Base() {
        super();
    }
}
