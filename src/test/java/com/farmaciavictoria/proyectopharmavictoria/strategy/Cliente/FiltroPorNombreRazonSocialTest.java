package com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FiltroPorNombreRazonSocialTest {
    @Test
    void filtraPorNombreCoincidente() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorNombreRazonSocial filtro = new FiltroPorNombreRazonSocial();
        List<Cliente> resultado = filtro.filtrar(clientes, "juan");
        assertEquals(1, resultado.size());
        assertEquals(c1, resultado.get(0));
    }

    @Test
    void filtraPorRazonSocialCoincidente() {
        Cliente c1 = new Cliente("12345678", "", "");
        c1.setTipoCliente("Empresa");
        c1.setRazonSocial("Farmacia Victoria");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorNombreRazonSocial filtro = new FiltroPorNombreRazonSocial();
        List<Cliente> resultado = filtro.filtrar(clientes, "victoria");
        assertEquals(1, resultado.size());
        assertEquals(c1, resultado.get(0));
    }

    @Test
    void criterioVacioRetornaTodos() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorNombreRazonSocial filtro = new FiltroPorNombreRazonSocial();
        List<Cliente> resultado = filtro.filtrar(clientes, " ");
        assertEquals(2, resultado.size());
    }
}
