

package util;
import modelo.Venta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Generador de reportes en Excel
 */
public class ExcelReporte {

    public void generarReporteVentas(List<Venta> ventas, File outputFile) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            // Hoja 1: Datos detallados
            Sheet sheet = workbook.createSheet("Ventas Detalladas");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Fecha", "Vehículo", "Cliente", "Vendedor", "Precio Final"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int rowNum = 1;
            double totalVendido = 0;

            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(venta.getIdVenta());
                row.createCell(1).setCellValue(sdf.format(venta.getFechaVenta()));
                row.createCell(2).setCellValue(venta.getNombreVehiculo());
                row.createCell(3).setCellValue(venta.getNombreCliente());
                row.createCell(4).setCellValue(venta.getNombreVendedor());

                Cell moneyCell = row.createCell(5);
                moneyCell.setCellValue(venta.getPrecioFinal());
                moneyCell.setCellStyle(moneyStyle);

                totalVendido += venta.getPrecioFinal();

                // Aplicar estilos a todas las celdas
                for (int i = 0; i < 5; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
                row.getCell(1).setCellStyle(dateStyle);
            }

            // Fila de totales
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell labelCell = totalRow.createCell(4);
            labelCell.setCellValue("TOTAL:");
            labelCell.setCellStyle(headerStyle);

            Cell totalCell = totalRow.createCell(5);
            totalCell.setCellValue(totalVendido);
            totalCell.setCellStyle(moneyStyle);

            // Ajustar anchos de columna
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Hoja 2: Resumen
            crearHojaResumen(workbook, ventas);

            // Guardar archivo
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }

    private void crearHojaResumen(Workbook workbook, List<Venta> ventas) {
        Sheet sheet = workbook.createSheet("Resumen");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESUMEN EJECUTIVO");
        titleCell.setCellStyle(headerStyle);

        rowNum++; // Línea en blanco

        // KPIs
        double totalVendido = ventas.stream().mapToDouble(Venta::getPrecioFinal).sum();
        double ticketPromedio = !ventas.isEmpty() ? totalVendido / ventas.size() : 0;

        String[][] kpis = {
                {"Total de Ventas:", String.valueOf(ventas.size())},
                {"Total Vendido:", String.format("$%.2f", totalVendido)},
                {"Ticket Promedio:", String.format("$%.2f", ticketPromedio)}
        };

        for (String[] kpi : kpis) {
            Row row = sheet.createRow(rowNum++);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(kpi[0]);
            labelCell.setCellStyle(headerStyle);

            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(kpi[1]);
            valueCell.setCellStyle(dataStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.HAIR);
        style.setBorderTop(BorderStyle.HAIR);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }
}
