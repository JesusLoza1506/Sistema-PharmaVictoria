package com.farmaciavictoria.proyectopharmavictoria.command;

import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

public class RestablecerStockCommand implements Command {
    private final ProductoRepository productoRepository;
    private final DetalleVenta detalleVenta;

    public RestablecerStockCommand(ProductoRepository productoRepository, DetalleVenta detalleVenta) {
        this.productoRepository = productoRepository;
        this.detalleVenta = detalleVenta;
    }

    @Override
    public void execute() {
        Long productoId = Long.valueOf(detalleVenta.getProducto().getId());
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) {
            System.err.println("[COMMAND] Producto no encontrado para ID " + productoId);
            return;
        }
        int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
        int nuevoStock = stockActual + detalleVenta.getCantidad();
        productoRepository.updateStock(producto.getId(), nuevoStock);
        System.out.println("[COMMAND] Stock restablecido para producto ID " + producto.getId() + ": " + stockActual
                + " -> " + nuevoStock);
    }
}
