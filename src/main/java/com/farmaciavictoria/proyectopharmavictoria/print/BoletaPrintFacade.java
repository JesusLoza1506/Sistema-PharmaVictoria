package com.farmaciavictoria.proyectopharmavictoria.print;

import com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.Boleta;

public class BoletaPrintFacade {
    private final PrintStrategy printStrategy;

    public BoletaPrintFacade(PrintStrategy printStrategy) {
        this.printStrategy = printStrategy;
    }

    public void generarYImprimirBoleta(Boleta boleta) {
        printStrategy.generarYImprimir(boleta);
    }
}
