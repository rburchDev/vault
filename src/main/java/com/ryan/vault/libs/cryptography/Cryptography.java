package com.ryan.vault.libs.cryptography;


public interface Cryptography {
    String ALGORITHM = "SHA-1";
    String AES = "AES";

    default void prepareSecretKey(String password) {}

    default String crypt(String password, String secret) {
        return password;
    }
}
