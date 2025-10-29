package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

public class FacturaFactory implements ComprobanteFactory {
    @Override
    public Comprobante crearComprobante() {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipo("FACTURA");
        // Aquí puedes inicializar otros campos por defecto si lo necesitas
        return comprobante;
    }
}
