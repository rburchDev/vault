package com.ryan.vault.services.email;

public interface EmailService {

    String sendEmail(String to, String subject, String body);
}
