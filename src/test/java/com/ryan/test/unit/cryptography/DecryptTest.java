package com.ryan.test.unit.cryptography;

import com.ryan.vault.libs.cryptography.Decrypt;
import com.ryan.vault.exceptions.cryptography.CryptographyException;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public class DecryptTest {

    @Test
    public void testDecryption() throws CryptographyException {
        Decrypt decryptTest = new Decrypt();

        String pass = "eSdjX2DMOhWmWwjag0ySTw==";
        String sec = "SomeKey";

        String result = decryptTest.crypt(pass, sec);

        assertEquals("TestPassword", result);
    }
}
