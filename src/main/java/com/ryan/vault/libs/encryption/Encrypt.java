package com.ryan.vault.libs.encryption;

import com.ryan.vault.libs.base.Base;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.Base64;

/**
 * Public class to Encrypt and Decrypt based on algorithm provided
 */
public class Encrypt extends Base {
    private static SecretKeySpec secretKey;
    private static final String ALGORITHM = "SHA-1";
    private static final String AES = "AES";

    /**
     * helper function to set the secret key
     * @param password passed in password to establish the key with
     */
    private static void prepareSecretKey(String password) {
        MessageDigest sha;
        LOGGER.info("Preparing Key");
        try {
            byte[] key = password.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance(ALGORITHM);

            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

            LOGGER.info("Key prepared");

            secretKey = new SecretKeySpec(key, AES);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e);
        }
    }

    /**
     * method to encrypt the passed in password based on the secret
     * @param password String password to be encrypted
     * @param secret String secret to use to encrypt
     * @return Base64 encoded password or null
     */
    public String encrypt(String password, String secret) {
        LOGGER.info("Encrypting based on key");
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            LOGGER.info("Encrypting finished");

            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.warn(e);
        }
        return null;
    }

    /**
     * method to decrypt the passed in password based on the secret
     * @param password String password to be decrypted
     * @param secret String secret to use to decrypted
     * @return String of decrypted password or null
     */
    public String decrypt(String password, String secret) {
        LOGGER.info("Decrypting based on key");
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            LOGGER.info("Decrypting finished");

            return new String(cipher.doFinal(Base64.getDecoder().decode(password)));
        } catch (Exception e) {
            LOGGER.warn(e);
        }
        return null;
    }
}
