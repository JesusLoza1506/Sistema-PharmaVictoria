package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class ReporteVentasPorUsuarioDetalleController {
    @FXML
    private DatePicker dateInicio;
    @FXML
    private DatePicker dateFin;
    @FXML
    private TableView<UsuarioVentaDTO> tableUsuarios;
    @FXML
    private TableColumn<UsuarioVentaDTO, String> colUsuario;
    @FXML
    private TableColumn<UsuarioVentaDTO, String> colNombre;
    @FXML
    private TableColumn<UsuarioVentaDTO, String> colTotalVentas;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private Button btnExportarExcel;

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsuario()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        // Mostrar el monto con el prefijo S/.
        colTotalVentas.setCellValueFactory(cellData -> new SimpleStringProperty(
                "S/. " + String.format("%.2f", cellData.getValue().getTotalVentas())));
        // Opcional: cargar datos iniciales
        cargarVentasPorUsuario(null, null);
    }

    @FXML
    private void onFiltrar(ActionEvent event) {
        LocalDate inicio = dateInicio.getValue();
        LocalDate fin = dateFin.getValue();
        cargarVentasPorUsuario(inicio, fin);
    }

    private void cargarVentasPorUsuario(LocalDate inicio, LocalDate fin) {
        ObservableList<UsuarioVentaDTO> data = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.username, CONCAT(u.nombres, ' ', u.apellidos) AS nombre, SUM(v.total) AS totalVentas ");
        sql.append("FROM ventas v INNER JOIN usuarios u ON v.usuario_id = u.id ");
        sql.append("WHERE v.estado = 'REALIZADA' ");
        if (inicio != null)
            sql.append(" AND v.fecha_venta >= ? ");
        if (fin != null)
            sql.append(" AND v.fecha_venta <= ? ");
        sql.append("GROUP BY u.username, nombre ORDER BY totalVentas DESC");

        try (java.sql.Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance()
                .getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (inicio != null)
                ps.setObject(idx++, java.sql.Date.valueOf(inicio));
            if (fin != null)
                ps.setObject(idx++, java.sql.Date.valueOf(fin));
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioVentaDTO dto = new UsuarioVentaDTO();
                    dto.setUsuario(rs.getString("username"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setTotalVentas(rs.getDouble("totalVentas"));
                    data.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al consultar ventas por usuario");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo obtener los datos: " + e.getMessage());
            alert.showAndWait();
        }
        tableUsuarios.setItems(data);
    }

    @FXML
    private void onExportarPDF(ActionEvent event) {
        try {
            List<UsuarioVentaDTO> data = tableUsuarios.getItems();
            if (data == null || data.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Exportar PDF");
                alert.setHeaderText(null);
                alert.setContentText("No hay datos para exportar.");
                alert.showAndWait();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar a PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(tableUsuarios.getScene().getWindow());
            if (file != null) {
                com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade.exportarVentasPorUsuarioPDF(data,
                        file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportar PDF");
                alert.setHeaderText(null);
                alert.setContentText("PDF exportado correctamente en: " + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al exportar PDF");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo exportar el PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onExportarExcel(ActionEvent event) {
        try {
            List<UsuarioVentaDTO> data = tableUsuarios.getItems();
            if (data == null || data.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Exportar Excel");
                alert.setHeaderText(null);
                alert.setContentText("No hay datos para exportar.");
                alert.showAndWait();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar a Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
            File file = fileChooser.showSaveDialog(tableUsuarios.getScene().getWindow());
            if (file != null) {
                com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade.exportarVentasPorUsuarioExcel(data,
                        file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportar Excel");
                alert.setHeaderText(null);
                alert.setContentText("Excel exportado correctamente en: " + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al exportar Excel");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo exportar el Excel: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // El DTO real está en el paquete dto
}
