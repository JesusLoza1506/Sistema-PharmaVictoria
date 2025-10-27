package com.farmaciavictoria.proyectopharmavictoria.print;

import com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.Boleta;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfPrintStrategy implements PrintStrategy {
    // Genera solo el PDF, sin imprimir
    public void generarPDF(Boleta boleta, String pdfPath,
            String nombreFarmacia, String ruc, String direccion,
            String serie, String numero, String cajero,
            String metodoPago, String montoEnLetras,
            String fecha, String hora) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();
            // Fuente monoespaciada para simular ticket
            com.itextpdf.text.Font mono = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 9);
            // Logo (opcional)
            try {
                String logoPath = "src/main/resources/icons/logofarmacia.png";
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
                logo.scaleToFit(60, 60);
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception ex) {
                // No es crítico
            }
            // Encabezado alineado y con líneas
            StringBuilder sb = new StringBuilder();
            sb.append("┌──────────────────────────────────────────────────────────────┐\n");
            sb.append(String.format("│%-60s│\n",
                    nombreFarmacia != null ? nombreFarmacia : "BOTICA PHARMAVICTORIA S.A.C."));
            sb.append(String.format("│RUC: %-52s│\n", ruc != null ? ruc : "2064XXXXXXX"));
            sb.append(String.format("│Dirección: %-48s│\n",
                    direccion != null ? direccion : "Jr. Lima N° 456 – Huancayo"));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│%30s│\n", "BOLETA DE VENTA"));
            sb.append(String.format("│%30s│\n",
                    "SERIE: " + (serie != null ? serie : "B001") + " - N° " + (numero != null ? numero : "002314")));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│ FECHA: %s   HORA: %s │\n", fecha != null ? fecha : java.time.LocalDate.now(),
                    hora != null ? hora
                            : java.time.LocalTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))));
            sb.append(String.format("│ CAJERO: %s │\n", cajero != null ? cajero : "[Cajero]"));
            sb.append(String.format("│ CLIENTE: %s │\n",
                    boleta.getCliente() != null ? boleta.getCliente().getNombreCompleto() : "XXXXX"));
            sb.append(String.format("│ %s: %s │\n",
                    boleta.getCliente() != null && "EMPRESARIAL".equals(boleta.getCliente().getTipoCliente()) ? "RUC"
                            : "DNI",
                    boleta.getCliente() != null ? boleta.getCliente().getDocumento() : "XXXXX"));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append("│ CANT.  |  DESCRIPCIÓN                  | P.U.   | IMPORTE    │\n");
            sb.append("│--------------------------------------------------------------│\n");
            for (com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta det : boleta.getProductos()) {
                String prod = det.getProducto() != null ? det.getProducto().getNombre() : "";
                String linea = String.format("│ %-6d | %-28s | %-6.2f | %-9.2f │\n", det.getCantidad(), prod,
                        det.getPrecioUnitario(), det.getSubtotal());
                sb.append(linea);
            }
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│%40s%8.2f │\n", "SUBTOTAL:", boleta.getTotal() - boleta.getIgv()));
            sb.append(String.format("│%40s%8.2f │\n", "IGV (18%):", boleta.getIgv()));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│%40s%8.2f │\n", "TOTAL:", boleta.getTotal()));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│ Monto en letras: %s │\n",
                    montoEnLetras != null ? montoEnLetras : "[MONTO EN LETRAS]"));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append(String.format("│ MÉTODO DE PAGO: %s │\n", metodoPago != null ? metodoPago : "EFECTIVO"));
            sb.append("│--------------------------------------------------------------│\n");
            sb.append("│             ¡GRACIAS POR SU COMPRA! 💚                        │\n");
            sb.append("│   Consuma responsablemente. Medicamentos bajo receta médica.  │\n");
            sb.append("└──────────────────────────────────────────────────────────────┘\n");
            document.add(new com.itextpdf.text.Paragraph(sb.toString(), mono));
            document.close();
        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
        }
    }

    // Imprime el PDF generado
    public void imprimirPDF(String pdfPath) {
        try {
            java.io.File pdfFile = new java.io.File(pdfPath);
            if (!pdfFile.exists() || pdfFile.length() == 0) {
                System.err.println("Error: El PDF está vacío o no se generó correctamente. No se puede imprimir.");
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Error: El PDF está vacío o no se generó correctamente. No se puede imprimir.",
                        "Error de impresión", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Imprimir usando PDFBox y javax.print
            org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(pdfFile);
            org.apache.pdfbox.printing.PDFPageable pageable = new org.apache.pdfbox.printing.PDFPageable(document);
            javax.print.PrintService service = javax.print.PrintServiceLookup.lookupDefaultPrintService();
            if (service == null) {
                System.err.println("No se encontró impresora predeterminada.");
                javax.swing.JOptionPane.showMessageDialog(null, "No se encontró impresora predeterminada.",
                        "Error de impresión", javax.swing.JOptionPane.ERROR_MESSAGE);
                document.close();
                return;
            }
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setPageable(pageable);
            job.setPrintService(service);
            job.print();
            document.close();
        } catch (Exception ex) {
            System.err.println("Error al imprimir PDF: " + ex.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Error al imprimir PDF: " + ex.getMessage(),
                    "Error de impresión", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void generarYImprimir(Boleta boleta) {
        String pdfPath = "boleta_temp.pdf";
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            // Logo
            try {
                String logoPath = "src/main/resources/icons/logofarmacia.png";
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
                logo.scaleToFit(80, 80);
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception ex) {
                System.err.println("No se pudo cargar el logo: " + ex.getMessage());
            }

            // Encabezado
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("BOLETA DE VENTA", fontTitle);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);

            com.itextpdf.text.Paragraph ruc = new com.itextpdf.text.Paragraph("RUC: 10468894501");
            ruc.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(ruc);

            document.add(new com.itextpdf.text.Paragraph("Farmacia Victoria"));
            document.add(new com.itextpdf.text.Paragraph("Dirección: [Completar]"));
            document.add(new com.itextpdf.text.Paragraph("Teléfono: [Completar]"));
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Datos del cliente
            document.add(new com.itextpdf.text.Paragraph("Cliente: " + boleta.getCliente().getNombreCompleto()));
            document.add(new com.itextpdf.text.Paragraph(
                    (boleta.getCliente().getTipoCliente() != null
                            && "EMPRESARIAL".equals(boleta.getCliente().getTipoCliente()) ? "RUC: " : "DNI: ")
                            + boleta.getCliente().getDocumento()));
            // Usar la fecha actual, ya que Boleta no tiene fecha
            document.add(new com.itextpdf.text.Paragraph("Fecha: " + java.time.LocalDate.now()));
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Tabla de productos
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Precio Unitario");
            table.addCell("Subtotal");
            for (com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta item : boleta.getProductos()) {
                com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto producto = item.getProducto();
                table.addCell(producto != null ? producto.getNombre() : "");
                table.addCell(String.valueOf(item.getCantidad()));
                table.addCell(
                        item.getPrecioUnitario() != null ? String.format("S/ %.2f", item.getPrecioUnitario()) : "");
                table.addCell(item.getSubtotal() != null ? String.format("S/ %.2f", item.getSubtotal()) : "");
            }
            document.add(table);

            document.add(new com.itextpdf.text.Paragraph(" "));
            com.itextpdf.text.Paragraph total = new com.itextpdf.text.Paragraph(
                    "Total: S/ " + String.format("%.2f", boleta.getTotal()));
            total.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new com.itextpdf.text.Paragraph("Gracias por su compra!"));

            document.close();
        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            return;
        }

        // Enviar PDF a impresora predeterminada
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            java.io.File pdfFile = new java.io.File(pdfPath);
            if (desktop.isSupported(java.awt.Desktop.Action.PRINT)) {
                desktop.print(pdfFile);
            } else {
                System.out.println("Impresión no soportada en este sistema. PDF generado en: " + pdfPath);
            }
        } catch (Exception ex) {
            System.err.println("Error al imprimir PDF: " + ex.getMessage());
        }
    }
}
