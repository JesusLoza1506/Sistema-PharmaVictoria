package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

/**
 * Enum para las categorías de productos farmacéuticos
 */
public enum CategoriaProducto {
    ANALGESICOS("Analgésicos"),
    ANTIBIOTICOS("Antibióticos"),
    ANTIINFLAMATORIOS("Antiinflamatorios"),
    VITAMINAS("Vitaminas"),
    ANTIACIDOS("Antiácidos"),
    ANTIHISTAMINICOS("Antihistamínicos"),
    ANTIHIPERTENSIVOS("Antihipertensivos"),
    DIABETES("Diabetes"),
    RESPIRATORIO("Respiratorio"),
    DERMATOLOGIA("Dermatología"),
    HIGIENE("Higiene"),
    OTROS("Otros");

    private final String descripcion;

    CategoriaProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Compatibilidad para exportación
    public String getNombre() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}