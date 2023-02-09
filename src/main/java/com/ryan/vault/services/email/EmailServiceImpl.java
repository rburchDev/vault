package com.ryan.vault.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Class to implement the EmailService interface
 */
@Service
@Component
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}") private String sender;
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Method to send an email
     * @param to who is email going to
     * @param subject what is the subject of the email
     * @param body the body to be sent with the email
     * @return Either a Success string or Failed string
     */
    public String sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);

            this.mailSender.send(mailMessage);

            return "Success";
        } catch (Exception e) {
            return "Failed";
        }

    }

}
