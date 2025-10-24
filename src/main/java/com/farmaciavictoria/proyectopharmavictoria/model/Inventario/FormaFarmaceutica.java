package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

/**
 * Enum para las formas farmacéuticas
 */
public enum FormaFarmaceutica {
    TABLETA("Tableta"),
    CAPSULA("Cápsula"),
    JARABE("Jarabe"),
    SUSPENSION("Suspensión"),
    CREMA("Crema"),
    UNGÜENTO("Ungüento"),
    GOTAS("Gotas"),
    AMPOLLA("Ampolla"),
    SUPOSITORIO("Supositorio"),
    OTRO("Otro");

    private final String descripcion;

    FormaFarmaceutica(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}