package com.ryan.test.unit.utility;

import com.ryan.vault.libs.utility.CleanInputs;

import static org.junit.Assert.assertEquals;

import org.junit.*;

public class CleanTest {

    @Test
    public void testToLower() {
        CleanInputs cleanInputs = new CleanInputs();

        String site = "SITEUPPER";

        String result = cleanInputs.setSiteString(site);

        assertEquals(result, "siteupper");

    }

    @Test
    public void testRemoveWhiteSpace() {
        CleanInputs cleanInputs = new CleanInputs();

        String site ="white space";

        String result = cleanInputs.setSiteString(site);

        assertEquals(result, "whitespace");
    }
}
