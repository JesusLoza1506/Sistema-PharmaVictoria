package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import java.util.List;

public interface VentaService {
    Venta obtenerVentaPorId(int id);
    List<Venta> listarVentas();
    void registrarVenta(Venta venta);
    void actualizarVenta(Venta venta);
    void anularVenta(int id, String motivo, int usuarioId);
    // Otros métodos según necesidades
}
