package com.example.CRMAuthBackend.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailSenderService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.password}")
    private String password;

    public void send(String emailTo, String subject, String text) throws MessagingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "465"); // Yandex SMTP server port
        properties.setProperty("mail.smtp.auth", "true"); // enable authentication

        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", password);

        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.quitwait", "false");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.debug", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password); // sender username and password
            }
        });

        System.out.println(from);
        System.out.println(password);

        session.setDebug(true);

        // create message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
        message.setSubject(subject);
        message.setText(text);

        // send message
        Transport.send(message);
    }
}
