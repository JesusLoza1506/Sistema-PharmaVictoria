package com.farmaciavictoria.proyectopharmavictoria.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

final class ExportadorClientes {

    static void exportarClientesExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");
            String[] headers = { "Documento", "Tipo", "Razón Social / Nombre", "Teléfono", "Email", "Dirección",
                    "Fecha Nacimiento",
                    "Puntos Totales", "Puntos Usados", "Puntos Disponibles" };
            short verdeA = IndexedColors.BRIGHT_GREEN.getIndex();
            // Logo
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportadorClientes.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                    org.apache.poi.ss.usermodel.Drawing<?> drawing = sheet.createDrawingPatriarch();
                    org.apache.poi.ss.usermodel.ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
                    anchor.setCol1(0);
                    anchor.setRow1(logoRow);
                    anchor.setCol2(2);
                    anchor.setRow2(logoRow + 3);
                    drawing.createPicture(anchor, pictureIdx);
                    logoRow = 3;
                }
            } catch (Exception ex) {
                /* Si falla, ignora el logo */ }

            // Título
            Row titleRow = sheet.createRow(logoRow + 1);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Listado de Clientes");
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(verdeA);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(logoRow + 1, logoRow + 1, 0, headers.length - 1));

            // Cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(verdeA);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            Row header = sheet.createRow(logoRow + 2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000); // Ancho fijo para cada columna (ajustar si es necesario)
            }
            header.setHeightInPoints(28); // Alto de la fila de encabezado

            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            clientes.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente::getNombres,
                    java.text.Collator.getInstance()));
            for (int i = 0; i < clientes.size(); i++) {
                com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c = clientes.get(i);
                Row row = sheet.createRow(logoRow + 3 + i);
                row.createCell(0).setCellValue(c.getDocumento() != null ? c.getDocumento() : "");
                row.createCell(1).setCellValue(c.getTipoCliente() != null ? c.getTipoCliente() : "");
                row.createCell(2).setCellValue(
                        ("EMPRESA".equals(c.getTipoCliente()) ? c.getRazonSocial() : c.getNombreCompleto()));
                row.createCell(3).setCellValue(c.getTelefono() != null ? c.getTelefono() : "");
                row.createCell(4).setCellValue(c.getEmail() != null ? c.getEmail() : "");
                row.createCell(5).setCellValue(c.getDireccion() != null ? c.getDireccion() : "");
                row.createCell(6)
                        .setCellValue(c.getFechaNacimiento() != null ? c.getFechaNacimiento().format(fmt) : "");
                row.createCell(7).setCellValue(c.getPuntosTotales() != null ? c.getPuntosTotales() : 0);
                row.createCell(8).setCellValue(c.getPuntosUsados() != null ? c.getPuntosUsados() : 0);
                row.createCell(9).setCellValue(c.getPuntosDisponibles() != null ? c.getPuntosDisponibles() : 0);
                // Eliminada columna "Frecuente"
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(cellStyle);
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Pie de página
            Row footer = sheet.createRow(logoRow + 3 + clientes.size() + 1);
            Cell cellFooter = footer.createCell(0);
            cellFooter.setCellValue("Exportado el: " + java.time.LocalDate.now().format(fmt));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(footer.getRowNum(), footer.getRowNum(), 0,
                    headers.length - 1));
            cellFooter.setCellStyle(cellStyle);

            try (FileOutputStream fos = new FileOutputStream(nombreArchivo + ".xlsx")) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void exportarClientesPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes, String nombreArchivo) {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(
                    com.itextpdf.text.PageSize.A4.rotate());
            java.io.FileOutputStream fos = null;
            try {
                fos = new java.io.FileOutputStream(nombreArchivo + ".pdf");
            } catch (java.io.FileNotFoundException fnfe) {
                System.err.println("No se pudo crear el PDF. El archivo está abierto o bloqueado por otra aplicación.");
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No se pudo crear el PDF. El archivo está abierto o bloqueado por otra aplicación.",
                        "Error de exportación", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
            document.open();
            // Logo
            try {
                java.io.InputStream is = ExportadorClientes.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(logo);
                }
            } catch (Exception ex) {
                /* Si falla, ignora el logo */ }

            // Título
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    18, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(40, 167, 69));
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Listado de Clientes", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Tabla con cabecera estilizada
            String[] headers = { "Documento", "Tipo", "Razón Social / Nombre", "Teléfono", "Email", "Dirección",
                    "Fecha Nacimiento",
                    "Puntos Totales", "Puntos Usados", "Puntos Disponibles" };
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            // table.setWidths(columnWidths); // Eliminado duplicado, solo dejar la nueva
            // declaración

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    9, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    11, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(h, headerFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setBackgroundColor(new com.itextpdf.text.BaseColor(40, 167, 69));
                cell.setBorderColor(new com.itextpdf.text.BaseColor(32, 201, 151));
                cell.setPadding(7f);
                cell.setBorderWidth(1.5f);
                cell.setNoWrap(true); // Evita el wrap vertical
                cell.setFixedHeight(28f); // Alto fijo para la celda de encabezado
                table.addCell(cell);
            }

            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            clientes.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente::getNombres,
                    java.text.Collator.getInstance()));
            for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c : clientes) {
                table.addCell(new com.itextpdf.text.Phrase(c.getDocumento() != null ? c.getDocumento() : "", cellFont));
                table.addCell(
                        new com.itextpdf.text.Phrase(c.getTipoCliente() != null ? c.getTipoCliente() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        ("EMPRESA".equals(c.getTipoCliente()) ? c.getRazonSocial() : c.getNombreCompleto()),
                        cellFont));
                table.addCell(new com.itextpdf.text.Phrase(c.getTelefono() != null ? c.getTelefono() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(c.getEmail() != null ? c.getEmail() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(c.getDireccion() != null ? c.getDireccion() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        c.getFechaNacimiento() != null ? c.getFechaNacimiento().format(fmt) : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        c.getPuntosTotales() != null ? c.getPuntosTotales().toString() : "0", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        c.getPuntosUsados() != null ? c.getPuntosUsados().toString() : "0", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        c.getPuntosDisponibles() != null ? c.getPuntosDisponibles().toString() : "0", cellFont));
                // Eliminada columna "Frecuente"
            }
            document.add(table);
            float[] columnWidths = { 1.5f, 2f, 1.5f, 1.5f, 2f, 2f, 1.3f, 1.3f, 1.3f, 1.3f, 1.2f };
            com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                    "Exportado el: " + java.time.LocalDate.now().format(fmt), cellFont);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(footer);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
