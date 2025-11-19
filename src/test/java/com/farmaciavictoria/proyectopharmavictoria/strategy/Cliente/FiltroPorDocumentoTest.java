package com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FiltroPorDocumentoTest {
    @Test
    void filtraPorDocumentoCoincidente() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setDocumento("12345678");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setDocumento("87654321");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorDocumento filtro = new FiltroPorDocumento();
        List<Cliente> resultado = filtro.filtrar(clientes, "1234");
        assertEquals(1, resultado.size());
        assertEquals(c1, resultado.get(0));
    }

    @Test
    void criterioVacioRetornaTodos() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setDocumento("12345678");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setDocumento("87654321");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorDocumento filtro = new FiltroPorDocumento();
        List<Cliente> resultado = filtro.filtrar(clientes, "");
        assertEquals(2, resultado.size());
    }

    @Test
    void sinCoincidenciaRetornaVacio() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setDocumento("12345678");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setDocumento("87654321");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorDocumento filtro = new FiltroPorDocumento();
        List<Cliente> resultado = filtro.filtrar(clientes, "9999");
        assertTrue(resultado.isEmpty());
    }
}
