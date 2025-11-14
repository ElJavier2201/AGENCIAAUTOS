package util;
import modelo.Venta;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *Generador de reportes PDF profesionales
 */
public class PDFReporte {

    // Configuración de diseño
    private static final float MARGIN_LEFT = 50;
    private static final float MARGIN_RIGHT = 50;
    private static final float MARGIN_TOP = 50;
    private static final float MARGIN_BOTTOM = 50;

    // Colores corporativos
    private static final Color COLOR_PRIMARY = new Color(41, 128, 185);    // Azul
    private static final Color COLOR_SECONDARY = new Color(52, 73, 94);     // Gris oscuro
    private static final Color COLOR_ACCENT = new Color(46, 204, 113);      // Verde
    private static final Color COLOR_HEADER_BG = new Color(236, 240, 241);  // Gris claro

    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private float yPosition;

    /**
     * Genera un reporte de ventas completo
     */
    public void generarReporteVentas(List<Venta> ventas, File outputFile,
                                     String fechaInicio, String fechaFin) throws IOException {

        document = new PDDocument();
        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);

        contentStream = new PDPageContentStream(document, currentPage);
        yPosition = currentPage.getMediaBox().getHeight() - MARGIN_TOP;

        try {
            // 1. Encabezado con logo y datos de la empresa
            dibujarEncabezado();

            // 2. Título del reporte
            dibujarTitulo(fechaInicio, fechaFin);

            // 3. Resumen ejecutivo
            dibujarResumenEjecutivo(ventas);

            // 4. Tabla de ventas
            dibujarTablaVentas(ventas);

            // 5. Pie de página en todas las páginas
            agregarPiePagina();

            // Guardar documento
            document.save(outputFile);

        } finally {
            contentStream.close();
            document.close();
        }
    }

    /**
     * 🎨 Dibuja el encabezado corporativo
     */
    private void dibujarEncabezado() throws IOException {
        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Fondo de encabezado
        contentStream.setNonStrokingColor(COLOR_HEADER_BG);
        contentStream.addRect(0, yPosition - 10, currentPage.getMediaBox().getWidth(), 70);
        contentStream.fill();

        // Logo (si existe)
        try {
            PDImageXObject logo = PDImageXObject.createFromFile(
                    "src/recursos/logo_agencia.png", document);
            contentStream.drawImage(logo, MARGIN_LEFT, yPosition - 60, 60, 60);
        } catch (Exception e) {
            // Si no hay logo, dibujamos un placeholder
            contentStream.setNonStrokingColor(COLOR_PRIMARY);
            contentStream.addRect(MARGIN_LEFT, yPosition - 60, 60, 60);
            contentStream.fill();
        }

        // Datos de la empresa
        contentStream.setNonStrokingColor(COLOR_SECONDARY);
        contentStream.beginText();
        contentStream.setFont(fontBold, 16);
        contentStream.newLineAtOffset(MARGIN_LEFT + 80, yPosition - 20);
        contentStream.showText("AGENCIA DE AUTOS PREMIUM");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(fontNormal, 10);
        contentStream.newLineAtOffset(MARGIN_LEFT + 80, yPosition - 35);
        contentStream.showText("RFC: AAP123456789 | Tel: (222) 123-4567");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(fontNormal, 10);
        contentStream.newLineAtOffset(MARGIN_LEFT + 80, yPosition - 50);
        contentStream.showText("Email: ventas@agenciaautos.com | www.agenciaautos.com");
        contentStream.endText();

        // Fecha de generación (derecha)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaGeneracion = sdf.format(new Date());

        contentStream.beginText();
        contentStream.setFont(fontNormal, 9);
        float textWidth = fontNormal.getStringWidth("Generado: " + fechaGeneracion) / 1000 * 9;
        contentStream.newLineAtOffset(
                currentPage.getMediaBox().getWidth() - MARGIN_RIGHT - textWidth,
                yPosition - 20
        );
        contentStream.showText("Generado: " + fechaGeneracion);
        contentStream.endText();

        yPosition -= 90;
    }

    /**
     * 🎨 Dibuja el título del reporte
     */
    private void dibujarTitulo(String fechaInicio, String fechaFin) throws IOException {
        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Línea decorativa
        contentStream.setStrokingColor(COLOR_PRIMARY);
        contentStream.setLineWidth(3);
        contentStream.moveTo(MARGIN_LEFT, yPosition);
        contentStream.lineTo(currentPage.getMediaBox().getWidth() - MARGIN_RIGHT, yPosition);
        contentStream.stroke();

        yPosition -= 30;

        // Título principal
        contentStream.setNonStrokingColor(COLOR_PRIMARY);
        contentStream.beginText();
        contentStream.setFont(fontBold, 18);
        contentStream.newLineAtOffset(MARGIN_LEFT, yPosition);
        contentStream.showText("REPORTE DE VENTAS");
        contentStream.endText();

        yPosition -= 25;

        // Período del reporte
        contentStream.setNonStrokingColor(COLOR_SECONDARY);
        contentStream.beginText();
        contentStream.setFont(fontNormal, 11);
        contentStream.newLineAtOffset(MARGIN_LEFT, yPosition);

        String periodo = "Período: ";
        if (fechaInicio != null && fechaFin != null) {
            periodo += fechaInicio + " al " + fechaFin;
        } else {
            periodo += "Todos los registros";
        }
        contentStream.showText(periodo);
        contentStream.endText();

        yPosition -= 30;
    }

    /**
     * 📊 Dibuja el resumen ejecutivo con KPIs
     */
    private void dibujarResumenEjecutivo(List<Venta> ventas) throws IOException {
        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Calcular métricas
        int totalVentas = ventas.size();
        double totalVendido = ventas.stream().mapToDouble(Venta::getPrecioFinal).sum();
        double ticketPromedio = totalVentas > 0 ? totalVendido / totalVentas : 0;
        double ventaMasAlta = ventas.stream().mapToDouble(Venta::getPrecioFinal).max().orElse(0);

        // Título de sección
        contentStream.setNonStrokingColor(COLOR_SECONDARY);
        contentStream.beginText();
        contentStream.setFont(fontBold, 13);
        contentStream.newLineAtOffset(MARGIN_LEFT, yPosition);
        contentStream.showText("📊 RESUMEN EJECUTIVO");
        contentStream.endText();

        yPosition -= 25;

        // Tarjetas de KPIs
        float cardWidth = (currentPage.getMediaBox().getWidth() - MARGIN_LEFT - MARGIN_RIGHT - 30) / 4;
        float cardHeight = 70;
        float xOffset = MARGIN_LEFT;

        // KPI 1: Total Ventas
        dibujarTarjetaKPI(xOffset, yPosition, cardWidth, cardHeight,
                "Total Ventas", String.valueOf(totalVentas), COLOR_PRIMARY);

        xOffset += cardWidth + 10;

        // KPI 2: Total Vendido
        dibujarTarjetaKPI(xOffset, yPosition, cardWidth, cardHeight,
                "Total Vendido", String.format("$%.2f", totalVendido), COLOR_ACCENT);

        xOffset += cardWidth + 10;

        // KPI 3: Ticket Promedio
        dibujarTarjetaKPI(xOffset, yPosition, cardWidth, cardHeight,
                "Ticket Promedio", String.format("$%.2f", ticketPromedio), new Color(241, 196, 15));

        xOffset += cardWidth + 10;

        // KPI 4: Venta Más Alta
        dibujarTarjetaKPI(xOffset, yPosition, cardWidth, cardHeight,
                "Venta Más Alta", String.format("$%.2f", ventaMasAlta), new Color(231, 76, 60));

        yPosition -= cardHeight + 30;
    }

    /**
     * 🎴 Dibuja una tarjeta de KPI
     */
    private void dibujarTarjetaKPI(float x, float y, float width, float height,
                                   String label, String value, Color color) throws IOException {
        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Fondo de tarjeta con sombra
        contentStream.setNonStrokingColor(new Color(200, 200, 200, 50));
        contentStream.addRect(x + 2, y - height - 2, width, height);
        contentStream.fill();

        // Tarjeta principal
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.addRect(x, y - height, width, height);
        contentStream.fill();

        // Borde coloreado superior
        contentStream.setNonStrokingColor(color);
        contentStream.addRect(x, y - 5, width, 5);
        contentStream.fill();

        // Borde de tarjeta
        contentStream.setStrokingColor(new Color(220, 220, 220));
        contentStream.setLineWidth(1);
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();

        // Label
        contentStream.setNonStrokingColor(COLOR_SECONDARY);
        contentStream.beginText();
        contentStream.setFont(fontNormal, 9);
        float labelWidth = fontNormal.getStringWidth(label) / 1000 * 9;
        contentStream.newLineAtOffset(x + (width - labelWidth) / 2, y - 25);
        contentStream.showText(label);
        contentStream.endText();

        // Value
        contentStream.setNonStrokingColor(color);
        contentStream.beginText();
        contentStream.setFont(fontBold, 14);
        float valueWidth = fontBold.getStringWidth(value) / 1000 * 14;
        contentStream.newLineAtOffset(x + (width - valueWidth) / 2, y - 50);
        contentStream.showText(value);
        contentStream.endText();
    }

    /**
     * 📋 Dibuja la tabla de ventas con formato
     */
    private void dibujarTablaVentas(List<Venta> ventas) throws IOException {
        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Título de sección
        contentStream.setNonStrokingColor(COLOR_SECONDARY);
        contentStream.beginText();
        contentStream.setFont(fontBold, 13);
        contentStream.newLineAtOffset(MARGIN_LEFT, yPosition);
        contentStream.showText("📋 DETALLE DE VENTAS");
        contentStream.endText();

        yPosition -= 25;

        // Configuración de tabla
        float[] columnWidths = {40, 70, 150, 120, 120, 80}; // ID, Fecha, Vehículo, Cliente, Vendedor, Monto
        String[] headers = {"ID", "Fecha", "Vehículo", "Cliente", "Vendedor", "Monto"};
        float rowHeight = 20;
        float tableWidth = 0;
        for (float w : columnWidths) tableWidth += w;

        // Encabezado de tabla
        contentStream.setNonStrokingColor(COLOR_PRIMARY);
        contentStream.addRect(MARGIN_LEFT, yPosition - rowHeight, tableWidth, rowHeight);
        contentStream.fill();

        // Textos de encabezado
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.beginText();
        contentStream.setFont(fontBold, 9);

        float xOffset = MARGIN_LEFT + 5;
        for (int i = 0; i < headers.length; i++) {
            contentStream.newLineAtOffset(xOffset - contentStream.getCurrentPoint().x, yPosition - 14);
            contentStream.showText(headers[i]);
            xOffset += columnWidths[i];
        }
        contentStream.endText();

        yPosition -= rowHeight;

        // Filas de datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        boolean isOddRow = false;

        for (Venta venta : ventas) {
            // Verificar si necesitamos nueva página
            if (yPosition < MARGIN_BOTTOM + 50) {
                contentStream.close();
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                contentStream = new PDPageContentStream(document, currentPage);
                yPosition = currentPage.getMediaBox().getHeight() - MARGIN_TOP;
            }

            // Fondo alternado de fila
            if (isOddRow) {
                contentStream.setNonStrokingColor(new Color(249, 249, 249));
                contentStream.addRect(MARGIN_LEFT, yPosition - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
            }
            isOddRow = !isOddRow;

            // Datos de la fila
            String[] rowData = {
                    String.valueOf(venta.getIdVenta()),
                    sdf.format(venta.getFechaVenta()),
                    truncate(venta.getNombreVehiculo(), 22),
                    truncate(venta.getNombreCliente(), 18),
                    truncate(venta.getNombreVendedor(), 18),
                    String.format("$%.2f", venta.getPrecioFinal())
            };

            // Dibujar celdas
            contentStream.setNonStrokingColor(COLOR_SECONDARY);
            contentStream.beginText();
            contentStream.setFont(fontNormal, 8);

            xOffset = MARGIN_LEFT + 5;
            for (int i = 0; i < rowData.length; i++) {
                contentStream.newLineAtOffset(xOffset - contentStream.getCurrentPoint().x, yPosition - 13);
                contentStream.showText(rowData[i]);
                xOffset += columnWidths[i];
            }
            contentStream.endText();

            // Línea divisoria
            contentStream.setStrokingColor(new Color(220, 220, 220));
            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(MARGIN_LEFT, yPosition - rowHeight);
            contentStream.lineTo(MARGIN_LEFT + tableWidth, yPosition - rowHeight);
            contentStream.stroke();

            yPosition -= rowHeight;
        }

        // Borde de tabla
        contentStream.setStrokingColor(COLOR_PRIMARY);
        contentStream.setLineWidth(1);
        contentStream.addRect(MARGIN_LEFT, yPosition, tableWidth,
                (ventas.size() + 1) * rowHeight);
        contentStream.stroke();
    }

    /**
     * 📄 Agrega pie de página a todas las páginas
     */
    private void agregarPiePagina() throws IOException {
        PDType1Font fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        int pageNumber = 1;
        for (PDPage page : document.getPages()) {
            try (PDPageContentStream cs = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                // Línea decorativa
                cs.setStrokingColor(COLOR_PRIMARY);
                cs.setLineWidth(1);
                cs.moveTo(MARGIN_LEFT, MARGIN_BOTTOM + 20);
                cs.lineTo(page.getMediaBox().getWidth() - MARGIN_RIGHT, MARGIN_BOTTOM + 20);
                cs.stroke();

                // Texto del pie
                cs.setNonStrokingColor(new Color(150, 150, 150));
                cs.beginText();
                cs.setFont(fontItalic, 8);
                cs.newLineAtOffset(MARGIN_LEFT, MARGIN_BOTTOM + 5);
                cs.showText("Este documento es un reporte generado automáticamente por el sistema");
                cs.endText();

                // Número de página
                String pageText = "Página " + pageNumber + " de " + document.getNumberOfPages();
                float textWidth = fontItalic.getStringWidth(pageText) / 1000 * 8;
                cs.beginText();
                cs.setFont(fontItalic, 8);
                cs.newLineAtOffset(
                        page.getMediaBox().getWidth() - MARGIN_RIGHT - textWidth,
                        MARGIN_BOTTOM + 5
                );
                cs.showText(pageText);
                cs.endText();

                pageNumber++;
            }
        }
    }

    /**
     * Trunca texto si es muy largo
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}