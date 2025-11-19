package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FiltroPorRazonSocialTest {
    @Test
    void filtraPorRazonSocialCoincidente() {
        Proveedor p1 = new Proveedor();
        p1.setRazonSocial("Farmacia Victoria");
        Proveedor p2 = new Proveedor();
        p2.setRazonSocial("Botica Central");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRazonSocial filtro = new FiltroPorRazonSocial();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "victoria");
        assertEquals(1, resultado.size());
        assertEquals(p1, resultado.get(0));
    }

    @Test
    void criterioVacioRetornaTodos() {
        Proveedor p1 = new Proveedor();
        p1.setRazonSocial("Farmacia Victoria");
        Proveedor p2 = new Proveedor();
        p2.setRazonSocial("Botica Central");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRazonSocial filtro = new FiltroPorRazonSocial();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "");
        assertEquals(2, resultado.size());
    }

    @Test
    void sinCoincidenciaRetornaVacio() {
        Proveedor p1 = new Proveedor();
        p1.setRazonSocial("Farmacia Victoria");
        Proveedor p2 = new Proveedor();
        p2.setRazonSocial("Botica Central");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRazonSocial filtro = new FiltroPorRazonSocial();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "xyz");
        assertTrue(resultado.isEmpty());
    }
}
