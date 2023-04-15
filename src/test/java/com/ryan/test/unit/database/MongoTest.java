package com.ryan.test.unit.database;

import com.mongodb.client.MongoCollection;


import com.ryan.vault.libs.database.Mongo;
import com.ryan.vault.exceptions.mongo.MongoDbException;
import com.ryan.vault.exceptions.cryptography.CryptographyException;

import org.junit.*;

import static org.junit.Assert.*;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;


public class MongoTest {

    @Test
    public void documentSetTest() throws MongoDbException, CryptographyException {
        MongoCollection mongoCollection = PowerMockito.mock(MongoCollection.class);

        Mongo mongo = new Mongo();
        Mongo mongoSpy = Mockito.spy(mongo);
        Mockito.doReturn(mongoCollection).when(mongoSpy).mongoClient();

        String result = mongoSpy.setOne("test", "test", "test", true);

        assertEquals(result, "Success");

    }

}
