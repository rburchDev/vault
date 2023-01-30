package com.ryan.vault.libs.cryptography;

import com.ryan.vault.libs.base.Base;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.Base64;

/**
 * Public class to Decrypt based on algorithm provided
 */
public class Decrypt extends Base implements Cryptography {

    public static SecretKeySpec secretKey;

    /**
     * helper function to set the secret key
     * @param password passed in password to establish the key with
     */
    @Override
    public void prepareSecretKey(String password) {

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
     * method to decrypt the passed in password based on the secret
     * @param password String password to be decrypted
     * @param secret String secret to use to decrypted
     * @return String of decrypted password or null
     */
    @Override
    public String crypt(String password, String secret) {
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
