package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos;
import com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository;
import java.util.List;

public class HistorialComprasService {
    private final TransaccionPuntosRepository transaccionRepo;

    public HistorialComprasService() {
        this.transaccionRepo = new TransaccionPuntosRepository();
    }

    /**
     * Devuelve el historial de compras (transacciones GANADO) para un cliente
     */
    public List<TransaccionPuntos> obtenerComprasPorCliente(int clienteId) {
        List<TransaccionPuntos> todas = transaccionRepo.findByClienteId(clienteId);
        return todas.stream()
            .filter(t -> "GANADO".equalsIgnoreCase(t.getTipo()) && t.getVentaId() != null)
            .toList();
    }
}
