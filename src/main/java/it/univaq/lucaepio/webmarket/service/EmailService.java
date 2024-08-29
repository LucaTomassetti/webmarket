/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucat
 */

public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private final String username;
    private final String password;
    private final Properties prop;

    public EmailService() {
        prop = new Properties();
        // Abilita il debug
        prop.put("mail.debug", "true");
        prop.put("mail.debug.auth", "true");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                LOGGER.severe("Unable to find email.properties");
                throw new RuntimeException("Unable to find email.properties");
            }
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading email properties", ex);
            throw new RuntimeException("Error loading email properties", ex);
        }

        this.username = prop.getProperty("mail.username");
        this.password = prop.getProperty("mail.password");

        // Configurazione delle proprietà per la sessione di posta
        prop.put("mail.smtp.host", prop.getProperty("mail.smtp.host"));
        prop.put("mail.smtp.port", prop.getProperty("mail.smtp.port"));
        prop.put("mail.smtp.auth", prop.getProperty("mail.smtp.auth"));
        prop.put("mail.smtp.starttls.enable", prop.getProperty("mail.smtp.starttls.enable"));
    }

    public void sendEmail(String to, String subject, String body) {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Imposta l'output del debug su System.out
        session.setDebug(true);
        session.setDebugOut(System.out);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            LOGGER.info("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
        }
    }
}