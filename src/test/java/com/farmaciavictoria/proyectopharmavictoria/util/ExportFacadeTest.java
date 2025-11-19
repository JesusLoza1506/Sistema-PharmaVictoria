package com.farmaciavictoria.proyectopharmavictoria.util;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.io.File;

public class ExportFacadeTest {
    @Test
    void exportarClientesExcelListaVaciaNoFalla() {
        List<Cliente> clientes = Collections.emptyList();
        String archivo = "test-clientes-excel";
        ExportFacade.exportarClientesExcel(clientes, archivo);
        File f = new File(archivo + ".xlsx");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarClientesPDFListaVaciaNoFalla() {
        List<Cliente> clientes = Collections.emptyList();
        String archivo = "test-clientes-pdf";
        ExportFacade.exportarClientesPDF(clientes, archivo);
        File f = new File(archivo + ".pdf");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarClientesExcelConDatosMinimosNoFalla() {
        Cliente c = new Cliente("12345678", "Juan", "Perez");
        c.setTipoCliente("NATURAL");
        List<Cliente> clientes = Collections.singletonList(c);
        String archivo = "test-clientes-excel-min";
        ExportFacade.exportarClientesExcel(clientes, archivo);
        File f = new File(archivo + ".xlsx");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarClientesPDFConDatosMinimosNoFalla() {
        Cliente c = new Cliente("12345678", "Juan", "Perez");
        c.setTipoCliente("NATURAL");
        List<Cliente> clientes = Collections.singletonList(c);
        String archivo = "test-clientes-pdf-min";
        ExportFacade.exportarClientesPDF(clientes, archivo);
        File f = new File(archivo + ".pdf");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarExcelProductosVacioNoFalla() {
        List<Producto> productos = Collections.emptyList();
        String archivo = "test-productos-excel";
        ExportFacade.exportarExcel(productos, archivo);
        File f = new File(archivo + ".xlsx");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarPDFProductosVacioNoFalla() {
        List<Producto> productos = Collections.emptyList();
        String archivo = "test-productos-pdf";
        ExportFacade.exportarPDF(productos, archivo);
        File f = new File(archivo + ".pdf");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarProveedoresExcelVacioNoFalla() {
        List<Proveedor> proveedores = Collections.emptyList();
        String archivo = "test-proveedores-excel";
        ExportFacade.exportarProveedoresExcel(proveedores, archivo);
        File f = new File(archivo + ".xlsx");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarProveedoresPDFVacioNoFalla() {
        List<Proveedor> proveedores = Collections.emptyList();
        String archivo = "test-proveedores-pdf";
        ExportFacade.exportarProveedoresPDF(proveedores, archivo);
        File f = new File(archivo + ".pdf");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarUsuariosExcelVacioNoFalla() {
        List<Usuario> usuarios = Collections.emptyList();
        String archivo = "test-usuarios-excel";
        ExportFacade.exportarUsuariosExcel(usuarios, archivo);
        File f = new File(archivo + ".xlsx");
        if (f.exists())
            f.delete();
    }

    @Test
    void exportarUsuariosPDFVacioNoFalla() {
        List<Usuario> usuarios = Collections.emptyList();
        String archivo = "test-usuarios-pdf";
        ExportFacade.exportarUsuariosPDF(usuarios, archivo);
        File f = new File(archivo + ".pdf");
        if (f.exists())
            f.delete();
    }
}
