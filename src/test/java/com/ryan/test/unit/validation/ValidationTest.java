package com.ryan.test.unit.validation;

import com.mongodb.client.result.DeleteResult;

import com.ryan.vault.libs.validation.Validation;
import com.ryan.vault.exceptions.validation.NotFoundException;

import org.bson.Document;

import org.junit.*;

import static org.junit.Assert.*;

public class ValidationTest {

    @Test
    public void testCheckResponseSuccess() throws NotFoundException {
        Document docResponse = new Document();
        docResponse = docResponse.append("Response", "Success");

        Validation validation = new Validation();

        String message = "Success";

        Document result = validation.checkResponse(message);

        assertEquals(result, docResponse);
    }

    @Test
    public void testCheckResponseFailure() throws NotFoundException {
        Document docResponse = new Document();
        docResponse = docResponse.append("Response", "Failed");

        Validation validation = new Validation();

        String message = "FAIL";

        Document result = validation.checkResponse(message);

        assertEquals(result, docResponse);
    }

    @Test
    public void testCheckResponseNull() throws NotFoundException {
        try {
            Document docResponse = null;

            Validation validation = new Validation();

            Document result = validation.checkResponse(docResponse);
        } catch(NotFoundException e) {
            assertEquals(e.getMessage(), "Validation return a NULL response");
        }
    }

    @Test
    public void testCheckResponse() throws NotFoundException {
        Document docResponse = new Document();
        docResponse = docResponse.append("Response", "Something");

        Validation validation = new Validation();

        Document result = validation.checkResponse(docResponse);

        assertEquals(result, docResponse);
    }

    @Test
    public void testCheckResponseDelete() throws NotFoundException {
        DeleteResult response = DeleteResult.acknowledged(1);

        Validation validation = new Validation();

        String result = validation.checkResponse(response);

        assertEquals(result, "Success");
    }

    @Test
    public void testCheckResponseFailed() throws NotFoundException {
        DeleteResult response = DeleteResult.unacknowledged();

        Validation validation = new Validation();

        String result = validation.checkResponse(response);

        assertEquals(result, "Failure");
    }

    @Test
    public void testCheckStringTrue() {
        Validation validation = new Validation();

        String site = "github.com";

        boolean result = validation.checkSiteString(site);

        assertTrue(result);
    }

    @Test
    public void testCheckStringFalse() {
        Validation validation = new Validation();

        String site = "SomeSiteName";

        boolean result = validation.checkSiteString(site);

        assertFalse(result);
    }
}
