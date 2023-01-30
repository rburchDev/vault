package com.ryan.vault.api.api_controller;

import com.ryan.vault.libs.base.Base;
import com.ryan.vault.libs.database.Mongo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.bson.Document;

@RestController
public class ApiController extends Base {

    public ApiController() {}

    @GetMapping(value = "/getCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Document> getCreds(@RequestParam(value = "site") String site) {
        System.out.println(site);
        LOGGER.info("Getting Creds for:" + site);
        Document docResponse = Mongo.getOne("Site", site);

        return new ResponseEntity<>(docResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/setCredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Document> setCreds(@RequestParam String site,
                                             @RequestParam String username,
                                             @RequestParam String password
    ) {
        LOGGER.info("Setting Creds for: " + site);
        String response = Mongo.setOne(site, username, password);
        Document docResponse = new Document();
        if (response.equals("Success")) {
            docResponse = docResponse.append("Response", "Success");
        } else {
            docResponse = docResponse.append("Response", "Failed");
        }
        LOGGER.info(docResponse);
        return new ResponseEntity<>(docResponse, HttpStatus.ACCEPTED);
    }
}
