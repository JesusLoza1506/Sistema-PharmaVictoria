package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.util.List;
import java.io.File;

public class ExportadorPDFReporte implements ExportadorReporte {
    @Override
    public void exportar(List<VentaReporteDTO> datos, File destino) {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(destino));
            document.open();
            document.add(new com.itextpdf.text.Paragraph("Reporte de Ventas por Período",
                    com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18)));
            document.add(new com.itextpdf.text.Paragraph(" "));
            for (VentaReporteDTO venta : datos) {
                document.add(new com.itextpdf.text.Paragraph(
                        "Venta N°: " + venta.getNumeroBoleta() + " | Serie: " + venta.getSerie()));
                document.add(new com.itextpdf.text.Paragraph("Fecha: " + venta.getFechaVenta() + " | Cliente: "
                        + venta.getCliente() + " | Vendedor: " + venta.getVendedor()));
                document.add(new com.itextpdf.text.Paragraph(
                        "Total: S/. " + venta.getTotal() + " | Estado: " + venta.getEstado()));
                document.add(new com.itextpdf.text.Paragraph("Productos:"));
                com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
                table.addCell("Producto");
                table.addCell("Cantidad");
                table.addCell("Precio Unitario");
                table.addCell("Descuento");
                table.addCell("Subtotal");
                for (com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO prod : venta
                        .getProductos()) {
                    table.addCell(prod.getNombre());
                    table.addCell(String.valueOf(prod.getCantidad()));
                    table.addCell(prod.getPrecioUnitario().toPlainString());
                    table.addCell(prod.getDescuento().toPlainString());
                    table.addCell(prod.getSubtotal().toPlainString());
                }
                document.add(table);
                document.add(new com.itextpdf.text.Paragraph(" "));
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
