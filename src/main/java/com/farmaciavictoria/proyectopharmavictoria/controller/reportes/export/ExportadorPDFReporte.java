package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;

import java.io.File;

public class ExportadorPDFReporte implements ExportadorReporte {
    @Override
    public void exportar(List<VentaReporteDTO> datos, File destino) {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(destino));
            document.open();

            // Logo centrado
            String logoPath = "src/main/resources/icons/logofarmacia.png";
            try {
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
                logo.scaleToFit(100, 100);
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                // Si falla el logo, continuar sin él
            }

            // Título profesional
            com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("Reporte de Ventas por Período",
                    com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 20));
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new com.itextpdf.text.Paragraph(" "));

            for (VentaReporteDTO venta : datos) {
                com.itextpdf.text.Paragraph sub = new com.itextpdf.text.Paragraph(
                        "Venta N°: " + venta.getNumeroBoleta() + " | Serie: " + venta.getSerie(),
                        com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14));
                document.add(sub);
                document.add(new com.itextpdf.text.Paragraph("Fecha: " + venta.getFechaVenta() + " | Cliente: "
                        + venta.getCliente() + " | Vendedor: " + venta.getVendedor()));
                document.add(new com.itextpdf.text.Paragraph(
                        "Total: S/. " + venta.getTotal() + " | Estado: " + venta.getEstado()));
                document.add(new com.itextpdf.text.Paragraph("Productos:"));

                com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
                // Cabecera con fondo y fuente blanca
                com.itextpdf.text.BaseColor headerColor = new com.itextpdf.text.BaseColor(33, 150, 243);
                com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory
                        .getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.BaseColor.WHITE);
                String[] headers = { "Producto", "Cantidad", "Precio Unitario", "Descuento", "Subtotal" };
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                            new com.itextpdf.text.Phrase(h, headerFont));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    cell.setPadding(6);
                    table.addCell(cell);
                }
                // Datos
                for (com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.ProductoDetalleDTO prod : venta
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
