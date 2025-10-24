package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteHistorialCambioRepository;

import java.util.List;

public class ClienteHistorialCambioService {
    private final ClienteHistorialCambioRepository historialRepo;

    public ClienteHistorialCambioService() {
        this.historialRepo = new ClienteHistorialCambioRepository();
    }

    public void registrarCambio(ClienteHistorialCambio cambio) {
        historialRepo.save(cambio);
    }

    public List<ClienteHistorialCambio> obtenerHistorialPorCliente(int clienteId) {
        return historialRepo.findByClienteId(clienteId);
    }
}
