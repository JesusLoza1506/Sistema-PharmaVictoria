package com.farmaciavictoria.proyectopharmavictoria.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

final class ExportadorProveedores {

    static void exportarProveedoresExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores,
            String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Proveedores");
            String[] headers = { "Razón Social", "RUC", "Contacto", "Teléfono", "Condiciones Pago", "Estado" };
            short verdeA = IndexedColors.BRIGHT_GREEN.getIndex();
            // Logo
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportadorProveedores.class.getResourceAsStream("/icons/logofarmacia.png");
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

            // RUC
            Row rucRow = sheet.createRow(logoRow);
            Cell rucCell = rucRow.createCell(0);
            rucCell.setCellValue("RUC: 10468894501");
            Font rucFont = workbook.createFont();
            rucFont.setBold(true);
            rucFont.setColor(IndexedColors.DARK_GREEN.getIndex());
            CellStyle rucStyle = workbook.createCellStyle();
            rucStyle.setFont(rucFont);
            rucStyle.setAlignment(HorizontalAlignment.LEFT);
            rucCell.setCellStyle(rucStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(logoRow, logoRow, 0, headers.length - 1));

            // Título
            Row titleRow = sheet.createRow(logoRow + 1);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Listado de Proveedores");
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
            }

            // Ordenar por razón social
            proveedores.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor::getRazonSocial,
                    java.text.Collator.getInstance()));

            for (int i = 0; i < proveedores.size(); i++) {
                com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor p = proveedores.get(i);
                Row row = sheet.createRow(logoRow + 3 + i);
                row.createCell(0).setCellValue(p.getRazonSocial() != null ? p.getRazonSocial() : "");
                row.createCell(1).setCellValue(p.getRuc() != null ? p.getRuc() : "");
                row.createCell(2).setCellValue(p.getContacto() != null ? p.getContacto() : "");
                row.createCell(3).setCellValue(p.getTelefono() != null ? p.getTelefono() : "");
                row.createCell(4).setCellValue(p.getCondicionesPago() != null ? p.getCondicionesPago() : "");
                row.createCell(5).setCellValue(p.getActivo() != null && p.getActivo() ? "Activo" : "Inactivo");
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(cellStyle);
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Pie de página
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Row footer = sheet.createRow(logoRow + 3 + proveedores.size() + 1);
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

    static void exportarProveedoresPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores,
            String nombreArchivo) {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
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
                java.io.InputStream is = ExportadorProveedores.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(logo);
                }
            } catch (Exception ex) {
                /* Si falla, ignora el logo */ }

            // RUC
            com.itextpdf.text.Font rucFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12,
                    com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(40, 167, 69));
            com.itextpdf.text.Paragraph ruc = new com.itextpdf.text.Paragraph("RUC: 10468894501", rucFont);
            ruc.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(ruc);

            // Título
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    18, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(40, 167, 69));
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Listado de Proveedores", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Tabla con cabecera estilizada
            String[] headers = { "Razón Social", "RUC", "Contacto", "Teléfono", "Condiciones Pago", "Estado" };
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths = { 2.5f, 1.5f, 1.5f, 1.5f, 2f, 1.5f };
            table.setWidths(columnWidths);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    13, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
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
                table.addCell(cell);
            }

            // Ordenar por razón social
            proveedores.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor::getRazonSocial,
                    java.text.Collator.getInstance()));

            for (com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor p : proveedores) {
                table.addCell(
                        new com.itextpdf.text.Phrase(p.getRazonSocial() != null ? p.getRazonSocial() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getRuc() != null ? p.getRuc() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getContacto() != null ? p.getContacto() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getTelefono() != null ? p.getTelefono() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(p.getCondicionesPago() != null ? p.getCondicionesPago() : "",
                        cellFont));
                table.addCell(new com.itextpdf.text.Phrase(
                        p.getActivo() != null && p.getActivo() ? "Activo" : "Inactivo", cellFont));
            }
            document.add(table);
            // Pie de página
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
