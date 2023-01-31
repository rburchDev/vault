package com.ryan.vault.libs.cryptography;

import com.ryan.vault.libs.base.Base;
import com.ryan.vault.exceptions.cryptography.CryptographyException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.Base64;

/**
 * Public class to Encrypt based on algorithm provided
 */
public class Encrypt extends Base implements Cryptography {
    private static SecretKeySpec secretKey;

    /**
     * helper function to set the secret key
     * @param password passed in password to establish the key with
     * @throws CryptographyException throw error if algorithm issue occurs
     */
    @Override
    public void prepareSecretKey(String password) throws CryptographyException{
        LOGGER.info("Preparing Key");
        try {
            byte[] key = password.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance(ALGORITHM);

            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

            LOGGER.info("Key prepared");

            secretKey = new SecretKeySpec(key, AES);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Creating secret key error occurred");
            throw new CryptographyException("Error with Algorithm", e);
        }
    }

    /**
     * method to encrypt the passed in password based on the secret
     * @param password String password to be encrypted
     * @param secret String secret to use to encrypt
     * @return Base64 encoded password or null
     * @throws CryptographyException Throw exception if encrypting issue occurs
     */
    @Override
    public String crypt(String password, String secret) throws CryptographyException {
        LOGGER.info("Encrypting based on key");
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            LOGGER.info("Encrypting finished");

            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.error("Encrypting process threw error");
            throw new CryptographyException("Error with Encrypting", e);
        }
    }
}
