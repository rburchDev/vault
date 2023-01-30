package com.ryan.vault.models;

public class DocumentModel {
    public String UserName;
    public String Password;
    public String DateSet;

    public DocumentModel(String UserName, String Password, String DateSet) {
        this.UserName = UserName;
        this.Password = Password;
        this.DateSet = DateSet;
    }

}
