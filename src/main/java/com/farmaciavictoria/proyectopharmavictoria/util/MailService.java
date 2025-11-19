package com.farmaciavictoria.proyectopharmavictoria.util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.InputStream;

public class MailService {
    /**
     * Envía el correo de recuperación con el token
     */
    public void sendRecoveryTokenEmail(String to, String token) throws MessagingException {
        String subject = "Recuperación de contraseña - PharmaVictoria";
        String body = "Hola,\n\nTu código de recuperación es: " + token
                + "\n\nEste código es válido por 15 minutos.\nSi no solicitaste este cambio, ignora este mensaje.";
        System.out.println("Enviando correo de recuperación a: " + to + " con token: " + token);
        sendRecoveryEmail(to, subject, body);
        System.out.println("Correo de recuperación enviado (si no hay error en consola).");
    }

    private final Properties props = new Properties();
    private final String username;
    private final String password;

    public MailService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("recuperacion_email.properties")) {
            props.load(input);
            username = props.getProperty("mail.smtp.user");
            password = props.getProperty("mail.smtp.password");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar configuración SMTP de recuperación", e);
        }
    }

    public void sendRecoveryEmail(String to, String subject, String body) throws MessagingException {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", props.getProperty("mail.smtp.auth"));
        mailProps.put("mail.smtp.starttls.enable", props.getProperty("mail.smtp.starttls.enable"));
        mailProps.put("mail.smtp.host", props.getProperty("mail.smtp.host"));
        mailProps.put("mail.smtp.port", props.getProperty("mail.smtp.port"));

        Session session = Session.getInstance(mailProps, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username, "PharmaVictoria"));
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Error de codificación en el remitente", e);
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
