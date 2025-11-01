package com.farmaciavictoria.proyectopharmavictoria.reportes.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteVentasDetalleController {
    private static final Logger logger = LoggerFactory.getLogger(ReporteVentasDetalleController.class);
    @FXML
    private DatePicker dateInicio;
    @FXML
    private DatePicker dateFin;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private TableView<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO> tableVentas;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colNumero;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colSerie;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colFecha;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colCliente;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colVendedor;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colTotal;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colTipoPago;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colEstado;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO, String> colProductos;

    private com.farmaciavictoria.proyectopharmavictoria.reportes.strategy.ReporteVentasStrategy estrategia = new com.farmaciavictoria.proyectopharmavictoria.reportes.strategy.ReporteVentasDetalleStrategy();
    private java.util.List<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO> ventasFiltradas = new java.util.ArrayList<>();

    @FXML
    private void initialize() {
        // Configurar columnas para mostrar los datos del DTO
        colNumero.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNumeroBoleta()));
        colSerie.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSerie()));
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaVenta() != null ? data.getValue().getFechaVenta().toString() : ""));
        colCliente.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCliente()));
        colVendedor.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getVendedor()));
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTotal() != null ? data.getValue().getTotal().toPlainString() : ""));
        colTipoPago.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoPago()));
        colEstado.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
        colProductos.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getProductos() != null && !data.getValue().getProductos().isEmpty()
                                ? data.getValue().getProductos().stream()
                                        .map(p -> p.getNombre() + " x" + p.getCantidad())
                                        .reduce((a, b) -> a + ", " + b).orElse("")
                                : "Sin productos"));
    }

    @FXML
    private void onFiltrar() {
        LocalDate inicio = dateInicio.getValue();
        LocalDate fin = dateFin.getValue();
        logger.info("[REPORTE] Filtrando ventas desde {} hasta {}", inicio, fin);
        if (inicio == null || fin == null) {
            mostrarAlerta("Debes seleccionar ambas fechas para filtrar.");
            logger.warn("[REPORTE] Fechas no seleccionadas");
            return;
        }
        java.time.LocalDateTime inicioDT = inicio.atStartOfDay();
        java.time.LocalDateTime finDT = fin.atTime(23, 59, 59);
        try {
            ventasFiltradas = estrategia.obtenerReporte(inicioDT.toLocalDate(), finDT.toLocalDate());
            logger.info("[REPORTE] Ventas encontradas: {}", ventasFiltradas.size());
            tableVentas.getItems().setAll(ventasFiltradas);
            mostrarAlerta("Filtro aplicado: " + inicioDT + " a " + finDT + "\nResultados: " + ventasFiltradas.size());
            if (ventasFiltradas.isEmpty()) {
                mostrarAlerta("No se encontraron ventas en el período seleccionado.");
                logger.warn("[REPORTE] No se encontraron ventas en el período seleccionado");
            }
        } catch (Exception ex) {
            mostrarAlerta("Error al obtener ventas: " + ex.getMessage());
            logger.error("[REPORTE] Error al obtener ventas", ex);
        }
    }

    @FXML
    private void onExportarPDF() {
        try {
            if (ventasFiltradas == null || ventasFiltradas.isEmpty()) {
                System.out.println("[LOG] No hay datos para exportar PDF");
                mostrarAlerta("No hay datos para exportar.");
                return;
            }
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar reporte PDF");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("reporte_ventas.pdf");
            java.io.File destino = fileChooser.showSaveDialog(btnExportarPDF.getScene().getWindow());
            System.out.println("[LOG] FileChooser PDF destino: " + destino);
            if (destino != null) {
                String ruta = destino.getAbsolutePath();
                // Forzar extensión .pdf
                if (!ruta.toLowerCase().endsWith(".pdf")) {
                    ruta += ".pdf";
                }
                System.out.println("[LOG] Exportando PDF a: " + ruta);
                com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade.exportarReporteVentasAvanzado(
                        "PDF", ventasFiltradas, ruta);
                mostrarAlerta("Reporte PDF exportado correctamente en: " + ruta);
            } else {
                System.out.println("[LOG] FileChooser PDF cancelado por el usuario");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al exportar PDF: " + e.getMessage());
        }
    }

    @FXML
    private void onExportarExcel() {
        try {
            if (ventasFiltradas == null || ventasFiltradas.isEmpty()) {
                System.out.println("[LOG] No hay datos para exportar Excel");
                mostrarAlerta("No hay datos para exportar.");
                return;
            }
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar reporte Excel");
            fileChooser.getExtensionFilters()
                    .add(new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            fileChooser.setInitialFileName("reporte_ventas.xlsx");
            java.io.File destino = fileChooser.showSaveDialog(btnExportarExcel.getScene().getWindow());
            System.out.println("[LOG] FileChooser Excel destino: " + destino);
            if (destino != null) {
                String ruta = destino.getAbsolutePath();
                // Forzar extensión .xlsx
                if (!ruta.toLowerCase().endsWith(".xlsx")) {
                    ruta += ".xlsx";
                }
                System.out.println("[LOG] Exportando Excel a: " + ruta);
                com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade.exportarReporteVentasAvanzado(
                        "Excel", ventasFiltradas, ruta);
                mostrarAlerta("Reporte Excel exportado correctamente en: " + ruta);
            } else {
                System.out.println("[LOG] FileChooser Excel cancelado por el usuario");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error al exportar Excel: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al exportar Excel: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte de Ventas");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
