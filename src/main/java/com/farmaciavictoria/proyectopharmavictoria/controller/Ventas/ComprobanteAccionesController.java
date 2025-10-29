package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.net.URI;

public class ComprobanteAccionesController {
    @FXML
    private Label lblMensaje;
    @FXML
    private Button btnAbrirPDF;
    @FXML
    private Button btnEnviarWhatsApp;
    @FXML
    private Button btnEnviarGmail;
    @FXML
    private Button btnImprimir;

    private String pdfUrl;
    private String clienteNombre;
    private String clienteEmail;
    private String clienteTelefono;
    private Stage stage;

    public void setDatos(String pdfUrl, String clienteNombre, String clienteEmail, String clienteTelefono) {
        this.pdfUrl = pdfUrl;
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        // Mostrar mensaje según disponibilidad del PDF
        if (lblMensaje != null) {
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                lblMensaje.setText("¡Comprobante electrónico generado exitosamente!");
            } else {
                lblMensaje.setText("No se recibió el enlace PDF.");
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        btnAbrirPDF.setOnAction(e -> abrirPDF());
        btnEnviarWhatsApp.setOnAction(e -> enviarWhatsApp());
        btnEnviarGmail.setOnAction(e -> enviarGmail());
        btnImprimir.setOnAction(e -> imprimirPDF());
    }

    private void abrirPDF() {
        System.out.println("[DEBUG] abrirPDF - pdfUrl: " + pdfUrl);
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(pdfUrl));
                System.out.println("[DEBUG] PDF abierto correctamente: " + pdfUrl);
            } catch (Exception ex) {
                System.err.println("[ERROR] No se pudo abrir el PDF: " + ex.getMessage());
                lblMensaje.setText("No se pudo abrir el PDF: " + ex.getMessage());
            }
        } else {
            System.err.println("[ERROR] No se recibió el enlace PDF.");
            lblMensaje.setText("No se recibió el enlace PDF.");
        }
    }

    private void imprimirPDF() {
        System.out.println("[DEBUG] imprimirPDF - pdfUrl: " + pdfUrl);
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            java.io.File tempFile = null;
            try {
                // Descargar el PDF a un archivo temporal
                java.net.URL url = new java.net.URL(pdfUrl);
                tempFile = java.io.File.createTempFile("comprobante", ".pdf");
                try (java.io.InputStream in = url.openStream();
                        java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                // Imprimir usando PDFBox
                org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(tempFile);
                org.apache.pdfbox.printing.PDFPageable pageable = new org.apache.pdfbox.printing.PDFPageable(document);
                javax.print.PrintService service = javax.print.PrintServiceLookup.lookupDefaultPrintService();
                if (service == null) {
                    lblMensaje.setText("No se encontró impresora predeterminada.");
                    document.close();
                    return;
                }
                java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
                job.setPageable(pageable);
                job.setPrintService(service);
                job.print();
                document.close();
                lblMensaje.setText("Comprobante enviado a la impresora correctamente.");
            } catch (Exception ex) {
                System.err.println("[ERROR] No se pudo imprimir el PDF: " + ex.getMessage());
                lblMensaje.setText("No se pudo imprimir el PDF: " + ex.getMessage());
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        } else {
            System.err.println("[ERROR] No se recibió el enlace PDF.");
            lblMensaje.setText("No se recibió el enlace PDF.");
        }
    }

    private void enviarWhatsApp() {
        System.out.println("[DEBUG] enviarWhatsApp - pdfUrl: " + pdfUrl + ", telefono: " + clienteTelefono);
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            String mensaje = "Hola, aquí está tu comprobante electrónico: " + pdfUrl;
            String telefonoFormateado = (clienteTelefono != null && !clienteTelefono.trim().isEmpty())
                    ? clienteTelefono.replaceAll("[^0-9]", "")
                    : "";
            String url;
            if (!telefonoFormateado.isEmpty()) {
                url = "https://wa.me/" + telefonoFormateado + "?text="
                        + java.net.URLEncoder.encode(mensaje, java.nio.charset.StandardCharsets.UTF_8);
            } else {
                url = "https://wa.me/?text="
                        + java.net.URLEncoder.encode(mensaje, java.nio.charset.StandardCharsets.UTF_8);
            }
            try {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("[DEBUG] WhatsApp abierto correctamente: " + url);
            } catch (Exception ex) {
                System.err.println("[ERROR] No se pudo abrir WhatsApp: " + ex.getMessage());
                lblMensaje.setText("No se pudo abrir WhatsApp: " + ex.getMessage());
            }
        } else {
            System.err.println("[ERROR] No se recibió el enlace PDF.");
            lblMensaje.setText("No se recibió el enlace PDF.");
        }
    }

    private void enviarGmail() {
        System.out.println("[DEBUG] enviarGmail - pdfUrl: " + pdfUrl + ", clienteEmail: " + clienteEmail);
        if (pdfUrl != null && !pdfUrl.isEmpty() && clienteEmail != null && !clienteEmail.isEmpty()) {
            String asunto = "Comprobante electrónico";
            String cuerpo = "Hola " + (clienteNombre != null ? clienteNombre : "")
                    + ",\nAdjunto el comprobante electrónico en PDF.";
            java.io.File tempFile = null;
            try {
                // Descargar PDF a archivo temporal
                java.net.URL url = new java.net.URL(pdfUrl);
                tempFile = java.io.File.createTempFile("comprobante", ".pdf");
                try (java.io.InputStream in = url.openStream();
                        java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                // Configuración SMTP (demo Gmail)
                String remitente = "lozayataco@gmail.com"; // Cambia por tu correo
                String password = "bahx gnoo ejji jgeh"; // Usa contraseña de aplicación Gmail
                String host = "smtp.gmail.com";
                int port = 587;
                java.util.Properties props = new java.util.Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.port", String.valueOf(port));
                jakarta.mail.Session session = jakarta.mail.Session.getInstance(props,
                        new jakarta.mail.Authenticator() {
                            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                                return new jakarta.mail.PasswordAuthentication(remitente, password);
                            }
                        });
                jakarta.mail.Message message = new jakarta.mail.internet.MimeMessage(session);
                message.setFrom(new jakarta.mail.internet.InternetAddress(remitente));
                message.setRecipients(jakarta.mail.Message.RecipientType.TO,
                        jakarta.mail.internet.InternetAddress.parse(clienteEmail));
                message.setSubject(asunto);
                // Crear parte de texto y adjunto
                jakarta.mail.Multipart multipart = new jakarta.mail.internet.MimeMultipart();
                jakarta.mail.BodyPart texto = new jakarta.mail.internet.MimeBodyPart();
                texto.setText(cuerpo);
                multipart.addBodyPart(texto);
                jakarta.mail.internet.MimeBodyPart adjunto = new jakarta.mail.internet.MimeBodyPart();
                adjunto.attachFile(tempFile);
                adjunto.setFileName("Comprobante.pdf");
                multipart.addBodyPart(adjunto);
                message.setContent(multipart);
                // Enviar correo
                jakarta.mail.Transport.send(message);
                lblMensaje.setText("Correo enviado correctamente a " + clienteEmail);
                System.out.println("[DEBUG] Correo enviado correctamente");
            } catch (Exception ex) {
                System.err.println("[ERROR] No se pudo enviar el correo: " + ex.getMessage());
                lblMensaje.setText("No se pudo enviar el correo: " + ex.getMessage());
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        } else {
            System.err.println("[ERROR] No se recibió el enlace PDF o email del cliente.");
            lblMensaje.setText("No se recibió el enlace PDF o email del cliente.");
        }
    }
}
