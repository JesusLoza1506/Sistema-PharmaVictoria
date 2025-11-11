package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.command;

import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

// Comando para restablecer el stock de un producto tras una venta cancelada
public class RestablecerStockCommand implements Command {
    // Repositorio de productos
    private final ProductoRepository productoRepository;
    // Detalle de la venta a revertir
    private final DetalleVenta detalleVenta;

    // Constructor: recibe el repositorio y el detalle de venta
    public RestablecerStockCommand(ProductoRepository productoRepository, DetalleVenta detalleVenta) {
        this.productoRepository = productoRepository;
        this.detalleVenta = detalleVenta;
    }

    // Ejecuta el comando: suma la cantidad al stock actual del producto
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
