package com.farmaciavictoria.proyectopharmavictoria.util;




final class ExportadorVentasPorUsuario {

    static void exportarVentasPorUsuarioExcel(
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO> usuarios,
            String rutaArchivo) {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Ventas por Usuario");
            String[] headers = { "Usuario", "Nombre", "Total Ventas (S/.)" };
            // Verde oscuro estándar de POI
            short verdeOscuro = org.apache.poi.ss.usermodel.IndexedColors.DARK_GREEN.getIndex();
            int logoRow = 0;
            try {
                java.io.InputStream is = ExportadorVentasPorUsuario.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    int pictureIdx = workbook.addPicture(bytes, org.apache.poi.ss.usermodel.Workbook.PICTURE_TYPE_PNG);
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

            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(logoRow + 1);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Ventas por Usuario");
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(verdeOscuro);
            titleStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(logoRow + 1, logoRow + 1, 0, headers.length - 1));

            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(verdeOscuro);
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
            cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cellStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.Row header = sheet.createRow(logoRow + 2);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }
            header.setHeightInPoints(28);

            for (int i = 0; i < usuarios.size(); i++) {
                com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO u = usuarios.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(logoRow + 3 + i);
                row.createCell(0).setCellValue(u.getUsuario() != null ? u.getUsuario() : "");
                row.createCell(1).setCellValue(u.getNombre() != null ? u.getNombre() : "");
                row.createCell(2).setCellValue("S/. " + String.format("%.2f", u.getTotalVentas()));
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(cellStyle);
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            org.apache.poi.ss.usermodel.Row footer = sheet.createRow(logoRow + 3 + usuarios.size() + 1);
            org.apache.poi.ss.usermodel.Cell cellFooter = footer.createCell(0);
            cellFooter.setCellValue("Exportado el: " + java.time.LocalDate.now().format(fmt));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(footer.getRowNum(), footer.getRowNum(), 0,
                    headers.length - 1));
            cellFooter.setCellStyle(cellStyle);

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(rutaArchivo)) {
                workbook.write(fos);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    static void exportarVentasPorUsuarioPDF(
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO> usuarios,
            String rutaArchivo) {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            java.io.FileOutputStream fos = null;
            try {
                fos = new java.io.FileOutputStream(rutaArchivo);
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
                java.io.InputStream is = ExportadorVentasPorUsuario.class.getResourceAsStream("/icons/logofarmacia.png");
                if (is != null) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(logo);
                }
            } catch (Exception ex) {
                /* Si falla, ignora el logo */ }

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    18, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(139, 195, 74));
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Ventas por Usuario", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));

            String[] headers = { "Usuario", "Nombre", "Total Ventas (S/.)" };
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            float[] columnWidths = { 2f, 3f, 2f };
            table.setWidths(columnWidths);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    13, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    11, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(h, headerFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setBackgroundColor(new com.itextpdf.text.BaseColor(139, 195, 74));
                cell.setBorderColor(new com.itextpdf.text.BaseColor(139, 195, 74));
                cell.setPadding(7f);
                cell.setBorderWidth(1.5f);
                table.addCell(cell);
            }

            for (com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.UsuarioVentaDTO u : usuarios) {
                table.addCell(new com.itextpdf.text.Phrase(u.getUsuario() != null ? u.getUsuario() : "", cellFont));
                table.addCell(new com.itextpdf.text.Phrase(u.getNombre() != null ? u.getNombre() : "", cellFont));
                table.addCell(
                        new com.itextpdf.text.Phrase("S/. " + String.format("%.2f", u.getTotalVentas()), cellFont));
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
}
