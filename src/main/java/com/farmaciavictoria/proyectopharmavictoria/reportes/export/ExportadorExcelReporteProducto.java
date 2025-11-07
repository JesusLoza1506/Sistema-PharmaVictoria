package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.view.VentaPorProductoDTO;
import java.util.List;
import java.io.File;

public class ExportadorExcelReporteProducto implements ExportadorReporteProducto {
    @Override
    public void exportar(List<VentaPorProductoDTO> datos, File destino) {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Ventas por Producto");
            int rowIdx = 0;

            // Logo en la primera fila (si es posible)
            try {
                java.io.InputStream is = new java.io.FileInputStream("src/main/resources/icons/logofarmacia.png");
                byte[] bytes = is.readAllBytes();
                int pictureIdx = workbook.addPicture(bytes, org.apache.poi.ss.usermodel.Workbook.PICTURE_TYPE_PNG);
                is.close();
                org.apache.poi.ss.usermodel.Drawing<?> drawing = sheet.createDrawingPatriarch();
                org.apache.poi.ss.usermodel.ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(rowIdx);
                anchor.setCol2(2);
                anchor.setRow2(rowIdx + 2);
                drawing.createPicture(anchor, pictureIdx);
                rowIdx += 3;
            } catch (Exception ex) {
                // Si falla el logo, continuar sin él
            }

            // Título profesional
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Reporte de Ventas por Producto");
            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 2));

            rowIdx++;

            // Cabecera con color naranja y fuente blanca
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor((short) 51); // Naranja
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            String[] headers = { "Producto", "Cantidad Vendida", "Total Vendido" };
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            for (VentaPorProductoDTO dto : datos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getProducto());
                row.createCell(1).setCellValue(dto.getCantidadVendida());
                row.createCell(2).setCellValue(dto.getTotalVentas());
                for (int i = 0; i < 3; i++)
                    row.getCell(i).setCellStyle(dataStyle);
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(destino)) {
                workbook.write(fos);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
