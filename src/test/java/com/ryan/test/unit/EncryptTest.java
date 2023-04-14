package com.ryan.test.unit;

import com.ryan.vault.libs.cryptography.Encrypt;
import com.ryan.vault.exceptions.cryptography.CryptographyException;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public class EncryptTest {

    @Test
    public void testEncryption() throws CryptographyException {
        Encrypt encryptTest = new Encrypt();

        String pass = "TestPassword";
        String sec = "SomeKey";

        String result = encryptTest.crypt(pass, sec);

        assertEquals("eSdjX2DMOhWmWwjag0ySTw==", result);
    }
}
