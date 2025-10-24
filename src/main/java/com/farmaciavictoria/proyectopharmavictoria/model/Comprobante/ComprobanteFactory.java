package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

public class ComprobanteFactory {
    public static Comprobante crear(String tipo, BoletaBuilder builder) {
        switch (tipo) {
            case "BOLETA":
                return builder.build();
            // case "FACTURA":
            //     return new FacturaBuilder().build();
            default:
                throw new IllegalArgumentException("Tipo de comprobante no soportado: " + tipo);
        }
    }
}
