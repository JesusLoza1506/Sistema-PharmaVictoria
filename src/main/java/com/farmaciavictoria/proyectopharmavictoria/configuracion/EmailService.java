package com.farmaciavictoria.proyectopharmavictoria.configuracion;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {
    public static void enviarCorreo(EmailConfig config, String asunto, String mensaje) throws Exception {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getSmtpServer());
            props.put("mail.smtp.port", String.valueOf(config.getSmtpPort()));
            props.put("mail.smtp.auth", "true");
            if (config.isUseSSL()) {
                props.put("mail.smtp.socketFactory.port", String.valueOf(config.getSmtpPort()));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            if (config.isUseTLS()) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getSmtpUser(), config.getSmtpPassword());
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getSmtpUser()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getDestinatario()));
            message.setSubject(asunto);
            message.setText(mensaje);
            Transport.send(message);
            System.out.println("[EMAIL] Correo enviado correctamente a: " + config.getDestinatario());
        } catch (Exception e) {
            System.err.println("[EMAIL][ERROR] No se pudo enviar el correo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
