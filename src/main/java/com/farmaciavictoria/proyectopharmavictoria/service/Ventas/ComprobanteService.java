package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import java.util.List;

public interface ComprobanteService {
    Comprobante obtenerPorId(int id);
    List<Comprobante> listarPorVentaId(int ventaId);
    void registrarComprobante(Comprobante comprobante);
    void actualizarComprobante(Comprobante comprobante);
    void eliminarComprobante(int id);
    // Otros métodos según necesidades
}
