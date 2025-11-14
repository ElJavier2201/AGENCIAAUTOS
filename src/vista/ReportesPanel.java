package vista;

import controlador.ReporteControlador;
import modelo.Venta;
import com.toedter.calendar.JDateChooser;
import util.ExcelReporte;
import util.FiltrosPanel;
import javax.swing.SwingWorker;
import util.PDFReporte;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


        btnExportarPDF = crearBoton("Exportar a PDF", new Color(231, 76, 60));
        btnRefrescar = crearBoton("Refrescar", new Color(149, 165, 166));
        JButton btnVistaPrevia = crearBoton("Vista Previa", new Color(155, 89, 182));
        btnVistaPrevia.addActionListener(e -> mostrarVistaPrevia());

        panelBotones.add(btnVistaPrevia);
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


    private void mostrarVistaPrevia() {
        if (listaVentas == null || listaVentas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para mostrar en la vista previa.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos filtrados actuales de la tabla
        List<Venta> ventasFiltradas = new ArrayList<>();
        for (int i = 0; i < tabla.getRowCount(); i++) {
            int idVenta = Integer.parseInt(tabla.getValueAt(i, 0).toString());
            listaVentas.stream()
                    .filter(venta -> venta.getIdVenta() == idVenta)
                    .findFirst().ifPresent(ventasFiltradas::add);
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ReportePrevio dialog = new ReportePrevio(owner, ventasFiltradas);
        dialog.setVisible(true);
    }

    /**
     *Crea panel con estadísticas resumidas
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
     *  Crea panel con búsqueda y filtros
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
        panelFiltros.add(new JLabel(" Desde:"));
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
        panelFiltros.add(new JLabel(" Vendedor:"));
        cbFiltroVendedor = new JComboBox<>(new String[]{"Todos"});
        cbFiltroVendedor.setPreferredSize(new Dimension(150, 25));
        cbFiltroVendedor.addActionListener(e -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));
        panelFiltros.add(cbFiltroVendedor);

        panelPrincipal.add(panelFiltros, BorderLayout.CENTER);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));
        panelFiltros.add(new JLabel(" Formato:"));
        JComboBox<String> cbFormato = new JComboBox<>(new String[]{"PDF", "Excel", "CSV"});
        cbFormato.setPreferredSize(new Dimension(100, 25));
        panelFiltros.add(cbFormato);

        return panelPrincipal;
    }

    /**
     * Aplica filtros de búsqueda
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
            JOptionPane.showMessageDialog(this,
                    "No hay datos para exportar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String formatoSeleccionado = (String) cbFormato.getSelectedItem();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");

        // Configurar extensión según formato
        switch (formatoSeleccionado) {
            case "PDF":
                fileChooser.setSelectedFile(new File("ReporteVentas.pdf"));
                break;
            case "Excel":
                fileChooser.setSelectedFile(new File("ReporteVentas.xlsx"));
                break;
            case "CSV":
                fileChooser.setSelectedFile(new File("ReporteVentas.csv"));
                break;
        }

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Mostrar diálogo de progreso
            JDialog progressDialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Generando Reporte...",
                    true
            );
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressDialog.add(progressBar);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);

            // Generar en hilo separado
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        // Obtener fechas para el reporte
                        String fechaInicioStr = dateDesde.getDate() != null
                                ? new SimpleDateFormat("dd/MM/yyyy").format(dateDesde.getDate())
                                : null;
                        String fechaFinStr = dateHasta.getDate() != null
                                ? new SimpleDateFormat("dd/MM/yyyy").format(dateHasta.getDate())
                                : null;

                        switch (formatoSeleccionado) {
                            case "PDF":
                                PDFReporte pdfGen = new PDFReporte();
                                pdfGen.generarReporteVentas(
                                        listaVentas,
                                        fileToSave,
                                        fechaInicioStr,
                                        fechaFinStr
                                );
                                break;

                            case "Excel":
                                ExcelReporte excelGen = new ExcelReporte();
                                excelGen.generarReporteVentas(listaVentas, fileToSave);
                                break;

                            case "CSV":
                                generarCSV(listaVentas, fileToSave);
                                break;
                        }
                        return true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void done() {
                    progressDialog.dispose();

                    try {
                        if (get()) {
                            // Preguntar si desea abrir el archivo
                            int resultado = JOptionPane.showConfirmDialog(
                                    ReportesPanel.this,
                                    " Reporte generado exitosamente.\n¿Desea abrirlo ahora?",
                                    "Exportación Completa",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            if (resultado == JOptionPane.YES_OPTION) {
                                try {
                                    Desktop.getDesktop().open(fileToSave);
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(
                                            ReportesPanel.this,
                                            "No se pudo abrir el archivo automáticamente.\n" +
                                                    "Ubicación: " + fileToSave.getAbsolutePath(),
                                            "Información",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(
                                    ReportesPanel.this,
                                    "❌ Error al generar el reporte",
                                    "Error de Exportación",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            worker.execute();

            // Mostrar el diálogo después de iniciar el worker
            SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));
        }
    }

    /**
     * Genera reporte en formato CSV
     */
    private void generarCSV(List<Venta> ventas, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // BOM para UTF-8 (para Excel)
            writer.write('\ufeff');

            // Encabezados
            writer.write("ID,Fecha,Vehículo,Cliente,Vendedor,Precio Final\n");

            // Datos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Venta v : ventas) {
                writer.write(String.format("%d,%s,\"%s\",\"%s\",\"%s\",%.2f\n",
                        v.getIdVenta(),
                        sdf.format(v.getFechaVenta()),
                        v.getNombreVehiculo().replace("\"", "\"\""),
                        v.getNombreCliente().replace("\"", "\"\""),
                        v.getNombreVendedor().replace("\"", "\"\""),
                        v.getPrecioFinal()
                ));
            }
        }
    }
}