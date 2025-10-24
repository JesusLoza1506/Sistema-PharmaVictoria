package com.farmaciavictoria.proyectopharmavictoria.service;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;

public class EmailService {
    private final String username;
    private final String password;
    private final Properties props;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        sendEmail(to, subject, text, null);
    }

    public void sendEmail(String to, String subject, String text, java.io.File adjunto) throws MessagingException {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        if (adjunto != null) {
            MimeBodyPart textoPart = new MimeBodyPart();
            textoPart.setText(text);
            MimeBodyPart adjuntoPart = new MimeBodyPart();
            adjuntoPart.setDataHandler(new DataHandler(new FileDataSource(adjunto)));
            adjuntoPart.setFileName(adjunto.getName());
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textoPart);
            multipart.addBodyPart(adjuntoPart);
            message.setContent(multipart);
        } else {
            message.setText(text);
        }
        Transport.send(message);
    }
}
