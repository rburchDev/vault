package com.ryan.vault.libs.cryptography;

import com.ryan.vault.exceptions.cryptography.CryptographyException;

public interface Cryptography {
    String ALGORITHM = "SHA-1";
    String AES = "AES";

    default void prepareSecretKey(String password) throws CryptographyException {}

    default String crypt(String password, String secret) throws CryptographyException {
        return password;
    }
}
