package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;

import java.io.File;

public class ExportadorExcelReporte implements ExportadorReporte {
    @Override
    public void exportar(List<VentaReporteDTO> datos, File destino) {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Ventas");
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
            titleCell.setCellValue("Reporte de Ventas por Período");
            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 11));

            rowIdx++;

            // Cabecera con color azul y fuente blanca
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor((short) 44); // Azul
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            String[] headers = { "N° Boleta", "Serie", "Fecha", "Cliente", "Vendedor", "Total", "Detalle Pago",
                    "Estado", "Productos", "Precio Unitario", "Descuento", "Subtotal" };
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

            for (VentaReporteDTO venta : datos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(venta.getNumeroBoleta());
                row.createCell(1).setCellValue(venta.getSerie());
                row.createCell(2).setCellValue(venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : "");
                row.createCell(3).setCellValue(venta.getCliente());
                row.createCell(4).setCellValue(venta.getVendedor());
                row.createCell(5)
                        .setCellValue(venta.getTotal() != null ? "S/" + String.format("%.2f", venta.getTotal()) : "");
                row.createCell(6).setCellValue(venta.getDetallePago());
                row.createCell(7).setCellValue(venta.getEstado());
                String productos = "";
                String precios = "";
                String descuentos = "";
                String subtotales = "";
                if (venta.getProductos() != null && !venta.getProductos().isEmpty()) {
                    productos = venta.getProductos().stream()
                            .map(p -> p.getNombre() + " x" + p.getCantidad())
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    precios = venta.getProductos().stream()
                            .map(p -> "S/" + String.format("%.2f", p.getPrecioUnitario()))
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    descuentos = venta.getProductos().stream()
                            .map(p -> "S/" + String.format("%.2f", p.getDescuento()))
                            .reduce((a, b) -> a + ", " + b).orElse("");
                    subtotales = venta.getProductos().stream()
                            .map(p -> "S/" + String.format("%.2f", p.getSubtotal()))
                            .reduce((a, b) -> a + ", " + b).orElse("");
                }
                row.createCell(8).setCellValue(productos);
                row.createCell(9).setCellValue(precios);
                row.createCell(10).setCellValue(descuentos);
                row.createCell(11).setCellValue(subtotales);
                for (int i = 0; i < 12; i++)
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
