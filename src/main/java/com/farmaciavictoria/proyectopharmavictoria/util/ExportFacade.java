package com.farmaciavictoria.proyectopharmavictoria.util;


import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ReporteTipo;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.io.File;
import java.util.List;

public class ExportFacade {

    public static void exportarUsuariosExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> usuarios, String nombreArchivo) {
        ExportadorUsuarios.exportarUsuariosExcel(usuarios, nombreArchivo);
    }

    public static void exportarUsuariosPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> usuarios, String nombreArchivo) {
        ExportadorUsuarios.exportarUsuariosPDF(usuarios, nombreArchivo);
    }

    public static void exportarClientesExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes, String nombreArchivo) {
        ExportadorClientes.exportarClientesExcel(clientes, nombreArchivo);
    }

    public static void exportarClientesPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes, String nombreArchivo) {
        ExportadorClientes.exportarClientesPDF(clientes, nombreArchivo);
    }

    public static void exportarProveedoresExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores,
            String nombreArchivo) {
        ExportadorProveedores.exportarProveedoresExcel(proveedores, nombreArchivo);
    }

    public static void exportarProveedoresPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores,
            String nombreArchivo) {
        ExportadorProveedores.exportarProveedoresPDF(proveedores, nombreArchivo);
    }

    public static void exportarExcel(List<Producto> productos, String nombreArchivo) {
        ExportadorInventario.exportarExcel(productos, nombreArchivo);
    }

    public static void exportarPDF(List<Producto> productos, String nombreArchivo) {
        ExportadorInventario.exportarPDF(productos, nombreArchivo);
    }

    public static void exportarVentasPorUsuarioExcel(
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO> usuarios,
            String rutaArchivo) {
        ExportadorVentasPorUsuario.exportarVentasPorUsuarioExcel(usuarios, rutaArchivo);
    }

    public static void exportarVentasPorUsuarioPDF(
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO> usuarios,
            String rutaArchivo) {
        ExportadorVentasPorUsuario.exportarVentasPorUsuarioPDF(usuarios, rutaArchivo);
    }

    public static void exportarReporteVentasAvanzado(String tipo,
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO> ventas,
            String nombreArchivo) {
        com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ExportadorReporte exportador = com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ExportadorReporteFactory
                .getExportador(tipo, ReporteTipo.VENTAS);
        exportador.exportar(ventas, new File(nombreArchivo));
    }
}
