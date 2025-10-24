package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos;
import com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository;
import java.util.List;

public class TransaccionPuntosService {
    private final TransaccionPuntosRepository repository = new TransaccionPuntosRepository();

    public List<TransaccionPuntos> obtenerHistorialPuntosPorCliente(int clienteId) {
        return repository.findByClienteId(clienteId);
    }
}
