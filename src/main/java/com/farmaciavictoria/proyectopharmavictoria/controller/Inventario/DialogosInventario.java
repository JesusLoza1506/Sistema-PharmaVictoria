package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.service.NotificationService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

record DialogosInventario(ProductoService productoService, NotificationService notificationService, Logger logger) {

    void verDetallesProducto(Producto producto) {
        try {
            // Refrescar el producto desde la base de datos antes de mostrar detalles
            Producto productoActualizado = productoService.obtenerPorId(producto.getId());
            FXMLLoader loader = new FXMLLoader(DialogosInventario.class.getResource("/fxml/Inventario/producto-detalles.fxml"));
            Parent root = loader.load();

            ProductoDetallesController controller = loader.getController();
            controller.setProducto(productoActualizado != null ? productoActualizado : producto);

            Stage stage = new Stage();
            stage.setTitle("Detalles del Producto - "
                    + (productoActualizado != null ? productoActualizado.getNombre() : producto.getNombre()));
            Scene scene = new Scene(root);

            try {
                String css = DialogosInventario.class.getResource("/css/Inventario/producto-detalles.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el CSS de detalles: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(500);
            stage.setMinHeight(400);
            stage.setWidth(800);
            stage.setHeight(700);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al abrir ventana de detalles: " + e.getMessage());
            e.printStackTrace();
            notificationService.mostrarError("No se pudo abrir la ventana de detalles del producto.");
        }
    }

    void ajustarStock(Producto producto) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(producto.getStockActual()));
        dialog.setTitle("Ajustar Stock");
        dialog.setHeaderText("Ajustar stock del producto: " + producto.getNombre());
        dialog.setContentText("Nuevo stock:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(stock -> {
            try {
                int stockAnterior = producto.getStockActual();
                int nuevoStock = Integer.parseInt(stock);
                if (producto.getId() != null && stockAnterior != nuevoStock) {
                    producto.setStockActual(nuevoStock);
                    productoService.actualizar(producto);
                    notificationService.mostrarExito("Stock actualizado correctamente");
                } else {
                    notificationService.mostrarError("No se realizó ningún cambio de stock.");
                }
            } catch (NumberFormatException e) {
                notificationService.mostrarError("Por favor ingrese un número válido");
            } catch (Exception e) {
                logger.error("Error al actualizar stock: {}", e.getMessage(), e);
                notificationService.mostrarError("No se pudo actualizar el stock: " + e.getMessage());
            }
        });
    }

    void cambiarUbicacion(Producto producto) {
        TextInputDialog dialog = new TextInputDialog(producto.getUbicacion());
        dialog.setTitle("Cambiar Ubicación");
        dialog.setHeaderText("Cambiar ubicación del producto: " + producto.getNombre());
        dialog.setContentText("Nueva ubicación:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ubicacion -> {
            try {
                producto.setUbicacion(ubicacion);
                productoService.actualizar(producto);
                notificationService.mostrarExito("Ubicación actualizada correctamente");
                // No necesario llamar loadData() - Observer Pattern se encarga
            } catch (Exception e) {
                logger.error("Error al actualizar ubicación: {}", e.getMessage(), e);
                notificationService.mostrarError("No se pudo actualizar la ubicación: " + e.getMessage());
            }
        });
    }

    void mostrarModalExportar(java.util.List<Producto> productosFiltrados) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogosInventario.class.getResource("/fxml/Inventario/exportar-inventario.fxml"));
            Parent root = loader.load();
            ExportarInventarioController exportController = loader.getController();
            exportController.setProductosFiltrados(new java.util.ArrayList<>(productosFiltrados));
            Stage stage = new Stage();
            stage.setTitle("Exportar Inventario / Reporte");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            notificationService.mostrarError("No se pudo abrir la ventana de exportación: " + ex.getMessage());
        }
    }

}
