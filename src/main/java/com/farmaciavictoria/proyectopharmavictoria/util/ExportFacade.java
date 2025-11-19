package com.farmaciavictoria.proyectopharmavictoria.util;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ReporteTipo;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.io.FileOutputStream;
import java.io.File;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportFacade {

    // Exportar usuarios a Excel
    public static void exportarUsuariosExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> usuarios, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usuarios");
            String[] headers = { "Usuario", "Nombres", "Apellidos", "DNI", "Teléfono", "Email", "Rol", "Estado" };
            short verdeA = IndexedColors.BRIGHT_GREEN.getIndex();
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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

            Row titleRow = sheet.createRow(logoRow + 1);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Listado de Usuarios");
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
                sheet.setColumnWidth(i, 5000);
            }
            header.setHeightInPoints(28);

            usuarios.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario::getNombres,
                    java.text.Collator.getInstance()));
            for (int i = 0; i < usuarios.size(); i++) {
                com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario u = usuarios.get(i);
                Row row = sheet.createRow(logoRow + 3 + i);
                row.createCell(0).setCellValue(u.getUsername() != null ? u.getUsername() : "");
                row.createCell(1).setCellValue(u.getNombres() != null ? u.getNombres() : "");
                row.createCell(2).setCellValue(u.getApellidos() != null ? u.getApellidos() : "");
                row.createCell(3).setCellValue(u.getDni() != null ? u.getDni() : "");
                row.createCell(4).setCellValue(u.getTelefono() != null ? u.getTelefono() : "");
                row.createCell(5).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                row.createCell(6).setCellValue(u.getRol() != null ? u.getRol().getDescripcion() : "");
                row.createCell(7).setCellValue(u.isActivo() ? "Activo" : "Inactivo");
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(cellStyle);
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Row footer = sheet.createRow(logoRow + 3 + usuarios.size() + 1);
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

    // Exportar usuarios a PDF
    public static void exportarUsuariosPDF(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> usuarios, String nombreArchivo) {
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
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(logo);
                }
            } catch (Exception ex) {
                /* Si falla, ignora el logo */ }

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    18, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(40, 167, 69));
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Listado de Usuarios", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));

            String[] headers = { "Usuario", "Nombres", "Apellidos", "DNI", "Teléfono", "Email", "Rol", "Estado" };
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths = { 1.5f, 2f, 2f, 1.5f, 1.5f, 2f, 1.5f, 2f };
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

            usuarios.sort(java.util.Comparator.comparing(
                    com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario::getNombres,
                    java.text.Collator.getInstance()));
            for (com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario u : usuarios) {
                table.addCell(new com.itextpdf.text.Phrase(u.getUsername() != null ? u.getUsername() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getNombres() != null ? u.getNombres() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getApellidos() != null ? u.getApellidos() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getDni() != null ? u.getDni() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getTelefono() != null ? u.getTelefono() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getEmail() != null ? u.getEmail() : "", cellFont));
                table.addCell(
                        new com.itextpdf.text.Phrase(u.getRol() != null ? u.getRol().getDescripcion() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.isActivo() ? "Activo" : "Inactivo", cellFont));
            }
            document.add(table);
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

    // Exportar clientes a Excel
    public static void exportarClientesExcel(
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
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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

    // Exportar clientes a PDF
    public static void exportarClientesPDF(
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
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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

    public static void exportarProveedoresExcel(
            List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores,
            String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Proveedores");
            String[] headers = { "Razón Social", "RUC", "Contacto", "Teléfono", "Condiciones Pago", "Estado" };
            short verdeA = IndexedColors.BRIGHT_GREEN.getIndex();
            // Logo
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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

    public static void exportarProveedoresPDF(
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
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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

    public static void exportarExcel(List<Producto> productos, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");
            String[] headers = { "Código", "Nombre", "Precio S/", "Stock", "Vencimiento", "Estado" };
            // Colores corporativos
            short verdeA = IndexedColors.BRIGHT_GREEN.getIndex();
            short verdeB = IndexedColors.LIGHT_GREEN.getIndex();
            // Estilo para cabecera
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

            // Estilo para celdas normales
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Logo y RUC
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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
            titleCell.setCellValue("Inventario de Productos");
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
            Row header = sheet.createRow(logoRow + 2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            productos.sort(java.util.Comparator.comparing(Producto::getNombre, java.text.Collator.getInstance()));
            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                Row row = sheet.createRow(logoRow + 3 + i);
                Cell c0 = row.createCell(0);
                c0.setCellValue(p.getCodigo());
                c0.setCellStyle(cellStyle);
                Cell c1 = row.createCell(1);
                c1.setCellValue(p.getNombre());
                c1.setCellStyle(cellStyle);
                Cell c2 = row.createCell(2);
                c2.setCellValue(p.getPrecioVenta().doubleValue());
                c2.setCellStyle(cellStyle);
                Cell c3 = row.createCell(3);
                c3.setCellValue(p.getStockActual());
                c3.setCellStyle(cellStyle);
                Cell c4 = row.createCell(4);
                c4.setCellValue(p.getFechaVencimiento() != null ? p.getFechaVencimiento().format(fmt) : "");
                c4.setCellStyle(cellStyle);
                Cell c5 = row.createCell(5);
                c5.setCellValue(p.getActivo() ? "Activo" : "Inactivo");
                c5.setCellStyle(cellStyle);
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Pie de página
            Row footer = sheet.createRow(logoRow + 3 + productos.size() + 1);
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

    public static void exportarPDF(List<Producto> productos, String nombreArchivo) {
        // Implementación funcional usando iText con logo, RUC y formato moderno
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(nombreArchivo + ".pdf"));
            document.open();
            // Logo
            try {
                java.io.InputStream is = ExportFacade.class.getResourceAsStream("/icons/logofarmacia.png");
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
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Inventario de Productos", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Tabla con cabecera estilizada
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths = { 1.5f, 3f, 1.5f, 1.5f, 2f, 1.5f };
            table.setWidths(columnWidths);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    13, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    11, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

            int verdeEncabezado = new com.itextpdf.text.BaseColor(40, 167, 69).getRGB();
            String[] headers = { "Código", "Nombre", "Precio S/", "Stock", "Vencimiento", "Estado" };
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
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            productos.sort(java.util.Comparator.comparing(Producto::getNombre, java.text.Collator.getInstance()));
            for (Producto p : productos) {
                com.itextpdf.text.pdf.PdfPCell c0 = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(p.getCodigo(), cellFont));
                com.itextpdf.text.pdf.PdfPCell c1 = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(p.getNombre(), cellFont));
                com.itextpdf.text.pdf.PdfPCell c2 = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(String.valueOf(p.getPrecioVenta()), cellFont));
                com.itextpdf.text.pdf.PdfPCell c3 = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(String.valueOf(p.getStockActual()), cellFont));
                com.itextpdf.text.pdf.PdfPCell c4 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(
                        p.getFechaVencimiento() != null ? p.getFechaVencimiento().format(fmt) : "", cellFont));
                com.itextpdf.text.pdf.PdfPCell c5 = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(p.getActivo() ? "Activo" : "Inactivo", cellFont));
                c0.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                c1.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                c2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                c3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                c4.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                c5.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                c0.setPadding(5f);
                c1.setPadding(5f);
                c2.setPadding(5f);
                c3.setPadding(5f);
                c4.setPadding(5f);
                c5.setPadding(5f);
                table.addCell(c0);
                table.addCell(c1);
                table.addCell(c2);
                table.addCell(c3);
                table.addCell(c4);
                table.addCell(c5);
            }
            document.add(table);
            // Pie de página
            com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                    "Exportado el: " + java.time.LocalDate.now().format(fmt), cellFont);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(footer);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Exportar ventas usando Factory para reportes avanzados
    public static void exportarReporteVentasAvanzado(String tipo,
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO> ventas,
            String nombreArchivo) {
        com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ExportadorReporte exportador = com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export.ExportadorReporteFactory
                .getExportador(tipo, ReporteTipo.VENTAS);
        exportador.exportar(ventas, new File(nombreArchivo));
    }
}
