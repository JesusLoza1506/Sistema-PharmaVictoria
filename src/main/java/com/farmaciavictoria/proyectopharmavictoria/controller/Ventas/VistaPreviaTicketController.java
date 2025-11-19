package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class VistaPreviaTicketController {
    @FXML
    private TextArea ticketTextArea;
    @FXML
    private Button btnImprimir;
    @FXML
    private Button btnGuardarPDF;

    private String ticketTexto;

    public void setTicketTexto(String texto) {
        this.ticketTexto = texto;
        if (ticketTextArea != null) {
            ticketTextArea.setText(texto);
        }
    }

    @FXML
    private void initialize() {
        if (ticketTexto != null) {
            ticketTextArea.setText(ticketTexto);
        }
    }

    @FXML

    private void onImprimir(ActionEvent event) {
        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(ticketTextArea.getScene().getWindow())) {
            boolean success = job.printPage(ticketTextArea);
            if (success) {
                job.endJob();
            }
        }
    }

    @FXML

    private void onGuardarPDF(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar ticket como PDF");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        java.io.File file = fileChooser.showSaveDialog(ticketTextArea.getScene().getWindow());
        if (file != null) {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                String texto = ticketTextArea.getText();
                // Usar iText para generar PDF
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
                document.open();
                com.itextpdf.text.Font font = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 10);
                document.add(new com.itextpdf.text.Paragraph(texto, font));
                document.close();
                // Mostrar mensaje de éxito
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("PDF guardado");
                alert.setHeaderText(null);
                alert.setContentText("El ticket se guardó exitosamente como PDF.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
