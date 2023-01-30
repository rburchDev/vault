package com.ryan.vault.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class VaultDocument {

    @Id
    private String id;

    private String site;
    private String userName;
    private String password;
    private String dateSet;

    public VaultDocument(String id, String site, String userName, String password, String dateSet) {
        super();
        this.id = id;
        this.site = site;
        this.userName = userName;
        this.password = password;
        this.dateSet = dateSet;
    }
}
