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

        String htmlBody = generarHtmlCorreo(subject, text);

        if (adjunto != null) {
            MimeBodyPart textoPart = new MimeBodyPart();
            textoPart.setContent(htmlBody, "text/html; charset=utf-8");
            MimeBodyPart adjuntoPart = new MimeBodyPart();
            adjuntoPart.setDataHandler(new DataHandler(new FileDataSource(adjunto)));
            adjuntoPart.setFileName(adjunto.getName());
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textoPart);
            multipart.addBodyPart(adjuntoPart);
            message.setContent(multipart);
        } else {
            message.setContent(htmlBody, "text/html; charset=utf-8");
        }
        Transport.send(message);
    }

    /**
     * Genera el cuerpo HTML con mejor diseño para el correo de alerta.
     */
    private String generarHtmlCorreo(String subject, String mensaje) {
        String color = subject.contains("Stock Bajo") ? "#ff9800" : "#e53935";
        String icono = subject.contains("Stock Bajo") ? "&#9888;" : "&#128308;";
        return "<div style='font-family:Arial,sans-serif;background:#f9f9f9;padding:24px;border-radius:12px;border:1px solid #eee;max-width:480px;margin:auto;'>"
                + "<h2 style='color:" + color + ";margin-bottom:8px;'>" + icono + " " + subject + "</h2>"
                + "<div style='font-size:16px;color:#333;margin-bottom:16px;'>" + mensaje + "</div>"
                + "<hr style='border:none;border-top:1px solid #eee;margin:16px 0;'>"
                + "<div style='font-size:13px;color:#888;'>"
                + "Este correo fue generado automáticamente por el sistema <b>PharmaVictoria</b>."
                + "</div>"
                + "</div>";
    }
}
