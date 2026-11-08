package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.time.LocalDate;

final class FilasProductos {
    private FilasProductos() {
    }

    static void configurar(TableView<Producto> tableProductos) {
        tableProductos.setRowFactory(tv -> {
            TableRow<Producto> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldProducto, newProducto) -> {
                if (newProducto == null) {
                    row.getStyleClass().removeAll("row-stock-bajo", "row-proximo-vencer",
                            "row-sin-stock", "row-vencido", "row-inactivo");
                } else {
                    // Limpiar estilos anteriores
                    row.getStyleClass().removeAll("row-stock-bajo", "row-proximo-vencer",
                            "row-sin-stock", "row-vencido", "row-inactivo");

                    // Aplicar estilos según el estado del producto
                    if (!newProducto.getActivo()) {
                        row.getStyleClass().add("row-inactivo");
                    } else if (newProducto.getFechaVencimiento() != null &&
                            newProducto.getFechaVencimiento().isBefore(LocalDate.now())) {
                        row.getStyleClass().add("row-vencido");
                    } else if (newProducto.getStockActual() == 0) {
                        row.getStyleClass().add("row-sin-stock");
                    } else if (newProducto.getStockActual() <= newProducto.getStockMinimo()) {
                        row.getStyleClass().add("row-stock-bajo");
                    } else if (newProducto.getFechaVencimiento() != null) {
                        int dias = com.farmaciavictoria.proyectopharmavictoria.service.ProductoService
                                .getDiasVencimientoAlerta();
                        java.time.LocalDate hoy = java.time.LocalDate.now();
                        java.time.LocalDate limite = hoy.plusDays(dias);
                        if (!newProducto.getFechaVencimiento().isBefore(hoy) &&
                                !newProducto.getFechaVencimiento().isAfter(limite)) {
                            row.getStyleClass().add("row-proximo-vencer");
                        }
                    }
                }
            });
            return row;
        });
    }
}
