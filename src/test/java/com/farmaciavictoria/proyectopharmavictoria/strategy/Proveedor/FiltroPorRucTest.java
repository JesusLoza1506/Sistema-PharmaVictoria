package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FiltroPorRucTest {
    @Test
    void filtraPorRucCoincidente() {
        Proveedor p1 = new Proveedor();
        p1.setRuc("20123456789");
        Proveedor p2 = new Proveedor();
        p2.setRuc("20987654321");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRuc filtro = new FiltroPorRuc();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "2012");
        assertEquals(1, resultado.size());
        assertEquals(p1, resultado.get(0));
    }

    @Test
    void criterioVacioRetornaTodos() {
        Proveedor p1 = new Proveedor();
        p1.setRuc("20123456789");
        Proveedor p2 = new Proveedor();
        p2.setRuc("20987654321");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRuc filtro = new FiltroPorRuc();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "");
        assertEquals(2, resultado.size());
    }

    @Test
    void sinCoincidenciaRetornaVacio() {
        Proveedor p1 = new Proveedor();
        p1.setRuc("20123456789");
        Proveedor p2 = new Proveedor();
        p2.setRuc("20987654321");
        List<Proveedor> proveedores = Arrays.asList(p1, p2);
        FiltroPorRuc filtro = new FiltroPorRuc();
        List<Proveedor> resultado = filtro.filtrar(proveedores, "9999");
        assertTrue(resultado.isEmpty());
    }
}
