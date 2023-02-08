package com.ryan.vault.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class UpdateEmail implements EmailService {
    private final JavaMailSender mailSender;
    @Autowired
    public UpdateEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public String sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom("rtburch@outlook.com");
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
