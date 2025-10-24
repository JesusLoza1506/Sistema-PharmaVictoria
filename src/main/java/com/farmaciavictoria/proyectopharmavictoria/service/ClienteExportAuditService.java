package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteExportAudit;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteExportAuditRepository;

public class ClienteExportAuditService {
    private final ClienteExportAuditRepository repository = new ClienteExportAuditRepository();

    public void registrarExportacion(ClienteExportAudit audit) {
        repository.save(audit);
    }
}
