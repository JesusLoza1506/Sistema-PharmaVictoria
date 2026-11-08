package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepository;
import javafx.collections.FXCollections;

import java.util.function.Consumer;

final class DialogoHistorialVentas {
    private DialogoHistorialVentas() {
    }

    static void mostrar(VentaRepository ventaRepository, Consumer<Venta> anular,
            Consumer<String> mostrarMensaje) {
        // Obtener fecha límite (hace 24 horas)
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime hace24h = ahora.minusHours(24);
        // Obtener todas las ventas y filtrar por fecha
        java.util.List<Venta> ventas = ventaRepository.findAll();
        java.util.List<Venta> ventas24h = new java.util.ArrayList<>();
        for (Venta v : ventas) {
            if (v.getFechaVenta() != null && !v.getFechaVenta().isBefore(hace24h)) {
                ventas24h.add(v);
            }
        }
        // Crear ventana/modal
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Historial de ventas (últimas 24h)");
        javafx.scene.control.TableView<Venta> table = new javafx.scene.control.TableView<>();
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        // Columnas
        javafx.scene.control.TableColumn<Venta, String> colFecha = new javafx.scene.control.TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaVenta() != null ? data.getValue().getFechaVenta().toString() : ""));
        javafx.scene.control.TableColumn<Venta, String> colCliente = new javafx.scene.control.TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCliente() != null ? data.getValue().getCliente().getNombreCompleto() : ""));
        javafx.scene.control.TableColumn<Venta, String> colMonto = new javafx.scene.control.TableColumn<>("Total S/");
        colMonto.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTotal() != null ? data.getValue().getTotal().toPlainString() : ""));
        javafx.scene.control.TableColumn<Venta, String> colEstado = new javafx.scene.control.TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getEstado() != null ? data.getValue().getEstado() : ""));
        // Columna botón revertir/anular
        javafx.scene.control.TableColumn<Venta, Void> colRevertir = new javafx.scene.control.TableColumn<>(
                "Revertir/Anular");
        colRevertir.setCellFactory(tc -> new javafx.scene.control.TableCell<Venta, Void>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Anular");
            {
                btn.setStyle(
                        "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 8px;");
                btn.setOnAction(e -> {
                    Venta venta = getTableView().getItems().get(getIndex());
                    if (venta != null && !"ANULADA".equalsIgnoreCase(venta.getEstado())) {
                        // Solo llama a la lógica central, que ya maneja el feedback y el envío a
                        // NubeFacT
                        anular.accept(venta);
                        getTableView().refresh();
                    } else {
                        mostrarMensaje.accept("La venta ya está anulada.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Venta venta = empty ? null : getTableView().getItems().get(getIndex());
                setGraphic((!empty && venta != null && !"ANULADA".equalsIgnoreCase(venta.getEstado())) ? btn : null);
            }
        });
        table.getColumns().addAll(colFecha, colCliente, colMonto, colEstado, colRevertir);
        table.setItems(FXCollections.observableArrayList(ventas24h));
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10, table);
        root.setStyle("-fx-padding: 18px; -fx-background-color: #f8f9fa;");
        stage.setScene(new javafx.scene.Scene(root, 800, 400));
        stage.show();
    }
}
