package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.view.VentaPorProductoDTO;
import java.util.List;
import java.io.File;

public class ExportadorPDFReporteProducto implements ExportadorReporteProducto {
    @Override
    public void exportar(List<VentaPorProductoDTO> datos, File destino) {
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
            com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("Reporte de Ventas por Producto",
                    com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 20));
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new com.itextpdf.text.Paragraph(" "));

            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(3);
            // Cabecera con fondo naranja y fuente blanca
            com.itextpdf.text.BaseColor headerColor = new com.itextpdf.text.BaseColor(255, 152, 0);
            com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory
                    .getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.BaseColor.WHITE);
            String[] headers = { "Producto", "Cantidad Vendida", "Total Vendido" };
            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(h, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }
            // Datos
            for (VentaPorProductoDTO dto : datos) {
                table.addCell(dto.getProducto());
                table.addCell(String.valueOf(dto.getCantidadVendida()));
                table.addCell(String.valueOf(dto.getTotalVentas()));
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
