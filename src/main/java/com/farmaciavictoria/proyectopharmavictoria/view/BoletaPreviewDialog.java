package com.farmaciavictoria.proyectopharmavictoria.view;

import com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.Boleta;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class BoletaPreviewDialog {
    private final Boleta boleta;
    private final String nombreFarmacia;
    private final String ruc;
    private final String direccion;
    private final String serie;
    private final String numero;
    private final String cajero;
    private final String metodoPago;
    private final String montoEnLetras;
    private final String fecha;
    private final String hora;
    private boolean confirmado = false;
    private Consumer<Boleta> onConfirmCallback;

    private BoletaPreviewDialog(Builder builder) {
        this.boleta = builder.boleta;
        this.nombreFarmacia = builder.nombreFarmacia;
        this.ruc = builder.ruc;
        this.direccion = "Av. Ramos 418 - Imperial";
        this.serie = builder.serie;
        this.numero = builder.numero;
        this.cajero = builder.cajero;
        this.metodoPago = builder.metodoPago;
        this.montoEnLetras = builder.montoEnLetras;
        this.fecha = builder.fecha;
        this.hora = builder.hora;
        this.onConfirmCallback = builder.onConfirmCallback;
    }

    public static class Builder {
        private Boleta boleta;
        private String nombreFarmacia = "BOTICA PHARMAVICTORIA S.A.C.";
        private String ruc = "2064XXXXXXX";
        private String direccion = "Av. Ramos 418 - Imperial";
        private String serie = "B001";
        private String numero = "002314";
        private String cajero = "[Cajero]";
        private String metodoPago = "EFECTIVO";
        private String montoEnLetras = "[MONTO EN LETRAS]";
        private String fecha = java.time.LocalDate.now().toString();
        private String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

        private Consumer<Boleta> onConfirmCallback;

        public Builder boleta(Boleta boleta) {
            this.boleta = boleta;
            return this;
        }

        public Builder onConfirm(Consumer<Boleta> callback) {
            this.onConfirmCallback = callback;
            return this;
        }

        public Builder nombreFarmacia(String nombreFarmacia) {
            this.nombreFarmacia = nombreFarmacia;
            return this;
        }

        public Builder ruc(String ruc) {
            this.ruc = ruc;
            return this;
        }

        public Builder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public Builder serie(String serie) {
            this.serie = serie;
            return this;
        }

        public Builder numero(String numero) {
            this.numero = numero;
            return this;
        }

        public Builder cajero(String cajero) {
            this.cajero = cajero;
            return this;
        }

        public Builder metodoPago(String metodoPago) {
            this.metodoPago = metodoPago;
            return this;
        }

        public Builder montoEnLetras(String montoEnLetras) {
            this.montoEnLetras = montoEnLetras;
            return this;
        }

        public Builder fecha(String fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder hora(String hora) {
            this.hora = hora;
            return this;
        }

        public BoletaPreviewDialog build() {
            return new BoletaPreviewDialog(this);
        }
    }

    public boolean mostrar() {
        VBox previewBox = new VBox(8);
        previewBox.setPadding(new Insets(18));
        previewBox.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-radius: 16; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, #26a69a, 16, 0.4, 0, 2);");
        Stage dialog = new Stage();

        VBox boletaContent = new VBox(2);
        boletaContent.setStyle(
                "-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b2dfdb, 8, 0.2, 0, 1);");

        boletaContent.getChildren().add(new Label("┌──────────────────────────────────────────────────────────────┐"));
        boletaContent.getChildren().add(new Label(String.format("│%40s│", nombreFarmacia)));
        boletaContent.getChildren().add(new Label(String.format("│%40s│", "RUC: " + ruc)));
        boletaContent.getChildren().add(new Label(String.format("│%40s│", "Dirección: " + direccion)));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label(String.format("│%30s│", "BOLETA DE VENTA")));
        boletaContent.getChildren().add(new Label(String.format("│%30s│", "SERIE: " + serie + " - N° " + numero)));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label(String.format("│ FECHA: %s   HORA: %s │", fecha, hora)));
        boletaContent.getChildren().add(new Label(String.format("│ CAJERO: %s │", cajero)));
        String cliente = boleta.getCliente() != null ? boleta.getCliente().getNombreCompleto() : "XXXXX";
        String tipoDoc = boleta.getCliente() != null && "EMPRESA".equals(boleta.getCliente().getTipoCliente())
                ? "RUC"
                : "DNI";
        String documento = boleta.getCliente() != null ? boleta.getCliente().getDocumento() : "XXXXX";
        boletaContent.getChildren().add(new Label(String.format("│ CLIENTE: %s │", cliente)));
        boletaContent.getChildren().add(new Label(String.format("│ %s: %s │", tipoDoc, documento)));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label("│ CANT.  |  DESCRIPCIÓN                  | P.U.   | IMPORTE    │"));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));

        for (com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta det : boleta.getProductos()) {
            String prod = det.getProducto() != null ? det.getProducto().getNombre() : "";
            String linea = String.format("│ %-6d | %-28s | %-6.2f | %-9.2f │", det.getCantidad(), prod,
                    det.getPrecioUnitario(), det.getSubtotal());
            boletaContent.getChildren().add(new Label(linea));
        }
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren()
                .add(new Label(String.format("│%40s%8.2f │", "SUBTOTAL:", boleta.getTotal() - boleta.getIgv())));
        boletaContent.getChildren().add(new Label(String.format("│%40s%8.2f │", "IGV (18%):", boleta.getIgv())));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label(String.format("│%40s%8.2f │", "TOTAL:", boleta.getTotal())));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label(String.format("│ Monto en letras: %s │", montoEnLetras)));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label(String.format("│ MÉTODO DE PAGO: %s │", metodoPago)));
        boletaContent.getChildren().add(new Label("│--------------------------------------------------------------│"));
        boletaContent.getChildren().add(new Label("│             ¡GRACIAS POR SU COMPRA! 💚                        │"));
        boletaContent.getChildren().add(new Label("│   Consuma responsablemente. Medicamentos bajo receta médica.  │"));
        boletaContent.getChildren().add(new Label("└──────────────────────────────────────────────────────────────┘"));

        previewBox.getChildren().add(boletaContent);

        // Botones con estilo y funcionalidad
        HBox buttonBar = new HBox(16);
        buttonBar.setPadding(new Insets(12, 0, 0, 0));
        buttonBar.setStyle("-fx-alignment: center;");

        Button btnDescargar = new Button("Descargar Boleta");
        btnDescargar.getStyleClass().add("btn-secondary");
        btnDescargar.setOnAction(e -> {
            try {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setInitialFileName("boleta_" + numero + ".pdf");
                fileChooser.getExtensionFilters()
                        .add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
                fileChooser.setTitle("Guardar boleta como PDF");
                Stage stage = (Stage) btnDescargar.getScene().getWindow();
                java.io.File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    // Generar PDF en la ruta elegida
                    String pdfPath = file.getAbsolutePath();
                    new com.farmaciavictoria.proyectopharmavictoria.print.PdfPrintStrategy().generarPDF(
                            boleta, pdfPath,
                            nombreFarmacia, ruc, direccion,
                            serie, numero, cajero,
                            metodoPago, montoEnLetras,
                            fecha, hora);
                    // Abrir el PDF generado
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                        if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                            desktop.open(file);
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error al guardar/abrir PDF: " + ex.getMessage());
            }
        });

        Button btnImprimir = new Button("Imprimir y Confirmar Venta");
        btnImprimir.getStyleClass().add("btn-primary");
        btnImprimir.setOnAction(e -> {
            confirmado = true;
            if (onConfirmCallback != null) {
                onConfirmCallback.accept(boleta);
            }
            dialog.close();
        });

        buttonBar.getChildren().addAll(btnDescargar, btnImprimir);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> {
            confirmado = false;
            dialog.close();
        });
        buttonBar.getChildren().add(btnCancelar);

        previewBox.getChildren().add(buttonBar);

        Scene scene = new Scene(previewBox);
        scene.getStylesheets().add(getClass().getResource("/css/Inventario/producto-form.css").toExternalForm());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Vista previa de Boleta");
        dialog.setScene(scene);
        dialog.showAndWait();
        return confirmado;
    }
}
