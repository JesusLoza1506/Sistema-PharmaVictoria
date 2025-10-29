package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

public class BoletaFactory implements ComprobanteFactory {
    @Override
    public Comprobante crearComprobante() {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipo("BOLETA");
        // Puedes inicializar otros campos por defecto si lo necesitas
        return comprobante;
    }
}
