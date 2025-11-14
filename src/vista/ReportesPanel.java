package vista;

import controlador.ReporteControlador;
import modelo.Venta;
import com.toedter.calendar.JDateChooser;
import util.FiltrosPanel;
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
import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Panel para mostrar el reporte de ventas.
 */
public class ReportesPanel extends JPanel {

    private final ReporteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JLabel lblTotalVendido;
    private final JLabel lblTotalVentas;
    private final JLabel lblTicketPromedio;
    private List<Venta> listaVentas;

    // Componentes de filtro y búsqueda
    private JDateChooser dateDesde;
    private JDateChooser dateHasta;
    private JButton btnFiltrar;
    private final JButton btnRefrescar;
    private final JButton btnExportarPDF;
    private FiltrosPanel filtrosPanel;
    private JComboBox<String> cbFiltroVendedor;

    public ReportesPanel() {
        controlador = new ReporteControlador();
        setLayout(new BorderLayout(10, 10));

        // ===== PANEL SUPERIOR (TÍTULO Y ESTADÍSTICAS) =====
        JPanel panelNorte = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("📊 Reporte General de Ventas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel panelStats = crearPanelEstadisticas();

        panelNorte.add(titulo, BorderLayout.NORTH);
        panelNorte.add(panelStats, BorderLayout.CENTER);
        add(panelNorte, BorderLayout.NORTH);

        // ===== PANEL DE BÚSQUEDA Y FILTROS =====
        JPanel panelBusquedaCompleto = crearPanelBusqueda();

        // ===== TABLA =====
        String[] columnas = {"ID Venta", "Fecha", "Vehículo", "Cliente", "Vendedor", "Precio Final"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelBusquedaCompleto, BorderLayout.NORTH);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // ===== PANEL INFERIOR (TOTALES Y BOTONES) =====
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(new Color(236, 240, 241));
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Totales
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        panelTotales.setBackground(new Color(236, 240, 241));

        lblTotalVentas = new JLabel("Total Ventas: 0");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblTotalVendido = new JLabel("Total Vendido: $0.00");
        lblTotalVendido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalVendido.setForeground(new Color(46, 204, 113));

        lblTicketPromedio = new JLabel("Ticket Promedio: $0.00");
        lblTicketPromedio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTicketPromedio.setForeground(new Color(52, 152, 219));

        panelTotales.add(lblTotalVentas);
        panelTotales.add(new JSeparator(SwingConstants.VERTICAL));
        panelTotales.add(lblTotalVendido);
        panelTotales.add(new JSeparator(SwingConstants.VERTICAL));
        panelTotales.add(lblTicketPromedio);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(new Color(236, 240, 241));

        btnExportarPDF = crearBoton("📄 Exportar a PDF", new Color(231, 76, 60));
        btnRefrescar = crearBoton("🔄 Refrescar", new Color(149, 165, 166));

        panelBotones.add(btnExportarPDF);
        panelBotones.add(btnRefrescar);

        panelSur.add(panelBotones, BorderLayout.WEST);
        panelSur.add(panelTotales, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);

        // ===== ACCIONES =====
        btnExportarPDF.addActionListener(e -> exportarAPDF());
        btnRefrescar.addActionListener(e -> {
            filtrosPanel.limpiarBusqueda();
            dateDesde.setDate(null);
            dateHasta.setDate(null);
            cbFiltroVendedor.setSelectedIndex(0);
            cargarReporte(null, null);
        });

        // Carga inicial
        cargarReporte(null, null);
    }

    /**
     * ✅ NUEVO: Crea panel con estadísticas resumidas
     */
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panel.add(crearTarjeta("Ventas Hoy", new Color(52, 152, 219), "📅"));
        panel.add(crearTarjeta("Ventas Este Mes", new Color(46, 204, 113), "📊"));
        panel.add(crearTarjeta("Ventas Este Año", new Color(241, 196, 15), "📈"));
        panel.add(crearTarjeta("Total Histórico", new Color(155, 89, 182), "💰"));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, Color color, String emoji) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblEmoji = new JLabel(emoji, SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel panelTexto = getJPanel(titulo);

        panel.add(lblEmoji, BorderLayout.WEST);
        panel.add(panelTexto, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel getJPanel(String titulo) {
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel("0", SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(Color.WHITE);

        JPanel panelTexto = new JPanel(new BorderLayout());
        panelTexto.setOpaque(false);
        panelTexto.add(lblTitulo, BorderLayout.NORTH);
        panelTexto.add(lblValor, BorderLayout.CENTER);
        return panelTexto;
    }

    /**
     * ✅ NUEVO: Crea panel con búsqueda y filtros
     */
    private JPanel crearPanelBusqueda() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Búsqueda básica
        String[] filtros = {"Todos", "Por Cliente", "Por Vehículo", "Por ID Venta"};
        filtrosPanel = new FiltrosPanel("Buscar venta...", filtros);
        filtrosPanel.setBusquedaTiempoReal(true);
        filtrosPanel.setOnSearch(this::aplicarFiltros);
        filtrosPanel.setOnFilterChange(f -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));

        panelPrincipal.add(filtrosPanel, BorderLayout.NORTH);

        // Filtros avanzados
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelFiltros.setBackground(new Color(236, 240, 241));
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Filtro por fechas
        panelFiltros.add(new JLabel("📅 Desde:"));
        dateDesde = new JDateChooser();
        dateDesde.setPreferredSize(new Dimension(120, 25));
        panelFiltros.add(dateDesde);

        panelFiltros.add(new JLabel("Hasta:"));
        dateHasta = new JDateChooser();
        dateHasta.setPreferredSize(new Dimension(120, 25));
        panelFiltros.add(dateHasta);

        btnFiltrar = new JButton("Aplicar");
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFiltrar.setBackground(new Color(52, 152, 219));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.addActionListener(e -> {
            Date sqlDesde = null;
            Date sqlHasta = null;

            if (dateDesde.getDate() != null) {
                sqlDesde = new Date(dateDesde.getDate().getTime());
            }
            if (dateHasta.getDate() != null) {
                sqlHasta = new Date(dateHasta.getDate().getTime());
            }
            cargarReporte(sqlDesde, sqlHasta);
        });
        panelFiltros.add(btnFiltrar);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Filtro por vendedor
        panelFiltros.add(new JLabel("👨‍💼 Vendedor:"));
        cbFiltroVendedor = new JComboBox<>(new String[]{"Todos"});
        cbFiltroVendedor.setPreferredSize(new Dimension(150, 25));
        cbFiltroVendedor.addActionListener(e -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));
        panelFiltros.add(cbFiltroVendedor);

        panelPrincipal.add(panelFiltros, BorderLayout.CENTER);

        return panelPrincipal;
    }

    /**
     * ✅ NUEVO: Aplica filtros de búsqueda
     */
    private void aplicarFiltros(String textoBusqueda) {
        if (listaVentas == null || listaVentas.isEmpty()) {
            return;
        }

        String busqueda = textoBusqueda.toLowerCase();
        String filtroTexto = filtrosPanel.getFiltroSeleccionado();
        String vendedorSeleccionado = (String) cbFiltroVendedor.getSelectedItem();

        List<Venta> resultado = listaVentas.stream()
                .filter(v -> {
                    // Filtro de texto
                    boolean coincideTexto = busqueda.isEmpty() || coincideBusqueda(v, busqueda, filtroTexto);

                    // Filtro de vendedor
                    assert vendedorSeleccionado != null;
                    boolean coincideVendedor = vendedorSeleccionado.equals("Todos") ||
                            v.getNombreVendedor().equals(vendedorSeleccionado);

                    return coincideTexto && coincideVendedor;
                })
                .collect(Collectors.toList());

        actualizarTabla(resultado);
        actualizarTotales(resultado);

        tabla.getTableHeader().setToolTipText(
                resultado.size() + " resultado(s) de " + listaVentas.size() + " total"
        );
    }

    private boolean coincideBusqueda(Venta v, String busqueda, String filtro) {
        return switch (filtro) {
            case "Por Cliente" -> v.getNombreCliente() != null && v.getNombreCliente().toLowerCase().contains(busqueda);
            case "Por Vehículo" ->
                    v.getNombreVehiculo() != null && v.getNombreVehiculo().toLowerCase().contains(busqueda);
            case "Por ID Venta" -> String.valueOf(v.getIdVenta()).contains(busqueda);
            default -> // "Todos"
                    (v.getNombreCliente() != null && v.getNombreCliente().toLowerCase().contains(busqueda)) ||
                            (v.getNombreVehiculo() != null && v.getNombreVehiculo().toLowerCase().contains(busqueda)) ||
                            (v.getNombreVendedor() != null && v.getNombreVendedor().toLowerCase().contains(busqueda)) ||
                            String.valueOf(v.getIdVenta()).contains(busqueda);
        };
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

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
                    listaVentas = get();
                    actualizarTabla(listaVentas);
                    actualizarTotales(listaVentas);
                    actualizarVendedoresCombo();
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

    private void actualizarTabla(List<Venta> ventas) {
        modeloTabla.setRowCount(0);
        for (Venta v : ventas) {
            modeloTabla.addRow(new Object[]{
                    v.getIdVenta(),
                    v.getFechaVenta(),
                    v.getNombreVehiculo(),
                    v.getNombreCliente(),
                    v.getNombreVendedor(),
                    String.format("$%.2f", v.getPrecioFinal())
            });
        }
    }

    private void actualizarTotales(List<Venta> ventas) {
        double total = ventas.stream().mapToDouble(Venta::getPrecioFinal).sum();
        double promedio = ventas.isEmpty() ? 0 : total / ventas.size();

        lblTotalVentas.setText("Total Ventas: " + ventas.size());
        lblTotalVendido.setText(String.format("Total Vendido: $%.2f", total));
        lblTicketPromedio.setText(String.format("Ticket Promedio: $%.2f", promedio));
    }

    private void actualizarVendedoresCombo() {
        cbFiltroVendedor.removeAllItems();
        cbFiltroVendedor.addItem("Todos");

        listaVentas.stream()
                .map(Venta::getNombreVendedor)
                .distinct()
                .sorted()
                .forEach(cbFiltroVendedor::addItem);
    }

    private void setBotonesEnabled(boolean enabled) {
        btnFiltrar.setEnabled(enabled);
        btnRefrescar.setEnabled(enabled);
        btnExportarPDF.setEnabled(enabled);
    }

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