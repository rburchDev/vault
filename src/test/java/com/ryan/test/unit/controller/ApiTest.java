package com.ryan.test.unit.controller;

import com.ryan.vault.api.api_controller.ApiController;

import org.bson.Document;

import org.junit.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class ApiTest {

    @Test
    public void testHealthCheck() {

        Document response = new Document();
        response.append("ping", "pong");

        ResponseEntity responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        ApiController apiController = new ApiController();

        ResponseEntity result = apiController.getHealth();

        assertEquals(result, responseEntity);
    }

}
