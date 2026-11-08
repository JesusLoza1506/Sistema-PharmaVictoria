package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

record ColumnasInventario(TableView<Producto> tableProductos, ProductoService productoService,
            DecimalFormat formatoMoneda, DateTimeFormatter formatoFecha,
            TableColumn<Producto, BigDecimal> colPrecioVenta, TableColumn<Producto, Integer> colStockActual,
            TableColumn<Producto, String> colFechaVencimiento, TableColumn<Producto, String> colNombre,
            TableColumn<Producto, String> colCodigo, TableColumn<Producto, Boolean> colEstado) {

    void configurar() {
        colPrecioVenta.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        colPrecioVenta.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            productoService.actualizarCampo(producto.getId(), "precio_venta", event.getNewValue());
            // Consultar el valor actualizado desde la base de datos
            Producto actualizado = productoService.obtenerPorId(producto.getId());
            if (actualizado != null) {
                producto.setPrecioVenta(actualizado.getPrecioVenta());
            } else {
                producto.setPrecioVenta(event.getNewValue());
            }
            tableProductos.refresh();
        });

        colStockActual.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colStockActual.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            producto.setStockActual(event.getNewValue());
            productoService.actualizarCampo(producto.getId(), "stock_actual", event.getNewValue());
            tableProductos.refresh();
        });

        colFechaVencimiento.setCellFactory(TextFieldTableCell.forTableColumn());
        colFechaVencimiento.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            try {
                LocalDate nuevaFecha = LocalDate.parse(event.getNewValue(), formatoFecha);
                productoService.actualizarCampo(producto.getId(), "fecha_vencimiento", nuevaFecha);
                // Consultar el valor actualizado desde la base de datos
                Producto actualizado = productoService.obtenerPorId(producto.getId());
                if (actualizado != null) {
                    producto.setFechaVencimiento(actualizado.getFechaVencimiento());
                } else {
                    producto.setFechaVencimiento(nuevaFecha);
                }
            } catch (Exception e) {
                // Si la fecha no es válida, no actualiza
            }
            tableProductos.refresh();
        });

        // Ejemplo para nombre, puedes replicar para los demás campos editables
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colNombre.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            producto.setNombre(event.getNewValue());
            productoService.actualizarCampo(producto.getId(), "nombre", event.getNewValue());
            tableProductos.refresh();
        });

        // Repetir para otros campos editables si lo necesitas (categoría, proveedor,
        // etc.)

        tableProductos.setEditable(true);
        // Configurar columnas básicas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Precio con formato y consulta en BD
        colPrecioVenta.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            BigDecimal precio = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                precio = actualizado != null ? actualizado.getPrecioVenta() : p.getPrecioVenta();
            } catch (Exception e) {
                precio = p.getPrecioVenta();
            }
            return new SimpleObjectProperty<>(precio);
        });
        colPrecioVenta.setCellFactory(column -> new TableCell<Producto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(item));
                }
            }
        });

        colStockActual.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            Integer stock = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                stock = actualizado != null ? actualizado.getStockActual() : p.getStockActual();
            } catch (Exception e) {
                stock = p.getStockActual();
            }
            return new SimpleObjectProperty<>(stock);
        });

        // Fecha de vencimiento consultando en BD
        colFechaVencimiento.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            LocalDate fecha = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                fecha = actualizado != null ? actualizado.getFechaVencimiento() : p.getFechaVencimiento();
            } catch (Exception e) {
                fecha = p.getFechaVencimiento();
            }
            if (fecha != null) {
                return new SimpleStringProperty(fecha.format(formatoFecha));
            }
            return new SimpleStringProperty("Sin fecha");
        });

        // Estado con colores y fondo especial si es inactivo
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colEstado.setCellFactory(column -> new TableCell<Producto, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-background-color: transparent;");
                    } else {
                        setText("Inactivo");
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-background-color: #F3F4F6;");
                    }
                }
            }
        });
    }
}
