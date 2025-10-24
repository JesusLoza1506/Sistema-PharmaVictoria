package com.farmaciavictoria.proyectopharmavictoria.print;

import com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.Boleta;

public interface PrintStrategy {
    void generarYImprimir(Boleta boleta);
}
