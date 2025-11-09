package vista;

import controlador.ReporteControlador;
import modelo.Venta;
import com.toedter.calendar.JDateChooser;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import javax.swing.SwingWorker;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Panel para mostrar el reporte de ventas (reemplaza al de la taquería).
 * (Actualizado con SwingWorker, Filtros de Fecha y Exportar PDF)
 */
public class ReportesPanel extends JPanel {

    private final ReporteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JLabel lblTotalVendido;

    // Componentes de filtro y botones
    private JDateChooser dateDesde;
    private JDateChooser dateHasta;
    private JButton btnFiltrar;
    private JButton btnRefrescar;
    private JButton btnExportarPDF;

    public ReportesPanel() {
        controlador = new ReporteControlador();
        setLayout(new BorderLayout(10, 10));

        // Panel de Filtros (Superior)
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Reporte General de Ventas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dateDesde = new JDateChooser();
        dateHasta = new JDateChooser();
        btnFiltrar = new JButton("Filtrar por Fecha");

        panelFiltros.add(new JLabel("Desde:"));
        panelFiltros.add(dateDesde);
        panelFiltros.add(new JLabel("Hasta:"));
        panelFiltros.add(dateHasta);
        panelFiltros.add(btnFiltrar);

        panelNorte.add(titulo);
        panelNorte.add(panelFiltros);
        add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Venta", "Fecha", "Vehículo", "Cliente", "Vendedor", "Precio Final"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        cargarReporte(null, null); // Carga inicial sin filtros

        // Panel de botones y total
        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefrescar = new JButton("Quitar Filtros");
        btnExportarPDF = new JButton("Exportar a PDF");

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnExportarPDF);

        lblTotalVendido = new JLabel("Total Vendido: $0.00");
        lblTotalVendido.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalVendido.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        panelSur.add(panelBotones, BorderLayout.WEST);
        panelSur.add(lblTotalVendido, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);

        // Acciones
        btnFiltrar.addActionListener(e -> {
            java.sql.Date sqlDesde = null;
            java.sql.Date sqlHasta = null;

            if (dateDesde.getDate() != null) {
                sqlDesde = new java.sql.Date(dateDesde.getDate().getTime());
            }
            if (dateHasta.getDate() != null) {
                sqlHasta = new java.sql.Date(dateHasta.getDate().getTime());
            }
            cargarReporte(sqlDesde, sqlHasta);
        });

        btnRefrescar.addActionListener(e -> {
            dateDesde.setDate(null);
            dateHasta.setDate(null);
            cargarReporte(null, null);
        });

        btnExportarPDF.addActionListener(e -> exportarAPDF());
    }

    /**
     * Carga el reporte en un hilo de fondo para no congelar la UI.
     */
    private void cargarReporte(java.sql.Date fechaInicio, java.sql.Date fechaFin) {
        setBotonesEnabled(false);
        lblTotalVendido.setText("Cargando reporte...");
        modeloTabla.setRowCount(0);

        SwingWorker<List<Venta>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Venta> doInBackground() throws Exception {
                return controlador.listarReporteVentas(fechaInicio, fechaFin);
            }

            @Override
            protected void done() {
                try {
                    List<Venta> ventas = get();
                    double total = 0.0;

                    for (Venta v : ventas) {
                        modeloTabla.addRow(new Object[]{
                                v.getIdVenta(),
                                v.getFechaVenta(),
                                v.getNombreVehiculo(),
                                v.getNombreCliente(),
                                v.getNombreVendedor(),
                                String.format("%.2f", v.getPrecioFinal())
                        });
                        total += v.getPrecioFinal();
                    }
                    lblTotalVendido.setText(String.format("Total Vendido: $ %.2f", total));

                } catch (InterruptedException | ExecutionException e) {
                    lblTotalVendido.setText("Error al cargar el reporte.");
                    JOptionPane.showMessageDialog(ReportesPanel.this,
                            "Error al cargar el reporte: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void setBotonesEnabled(boolean enabled) {
        btnFiltrar.setEnabled(enabled);
        btnRefrescar.setEnabled(enabled);
        btnExportarPDF.setEnabled(enabled);
    }

    /**
     * --- MÉTODO MODIFICADO (CORREGIDO PARA PDFBOX 3.0) ---
     */
    private void exportarAPDF() {
        if (tabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File("ReporteVentas.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (PDDocument document = new PDDocument()) {
                float margin = 50;
                float yStart = 750;
                float rowHeight = 20f;
                float[] colWidths = {50, 80, 200, 150, 150, 80};

                // Crear fuentes para PDFBox 3.0
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                float yPosition = yStart;

                // Cabecera
                contentStream.setFont(fontBold, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);

                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    contentStream.showText(modeloTabla.getColumnName(i) + "   ");
                    contentStream.newLineAtOffset(colWidths[i], 0);
                }
                contentStream.endText();
                yPosition -= rowHeight;

                // Datos
                contentStream.setFont(fontNormal, 10);

                for (int row = 0; row < modeloTabla.getRowCount(); row++) {
                    // Verificar si necesitamos nueva página
                    if (yPosition < margin) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(fontNormal, 10);
                        yPosition = yStart;
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);

                    for (int col = 0; col < modeloTabla.getColumnCount(); col++) {
                        String text = modeloTabla.getValueAt(row, col).toString();
                        if (text.length() > 30) text = text.substring(0, 27) + "...";

                        contentStream.showText(text + "   ");
                        contentStream.newLineAtOffset(colWidths[col], 0);
                    }
                    contentStream.endText();
                    yPosition -= rowHeight;
                }

                contentStream.close();

                document.save(fileToSave);
                JOptionPane.showMessageDialog(this, "Reporte PDF guardado exitosamente.",
                        "Exportación Completa", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el PDF: " + e.getMessage(),
                        "Error de Exportación", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}