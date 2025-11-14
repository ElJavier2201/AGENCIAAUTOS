package vista;

import controlador.ReporteControlador;
import modelo.Vendedor;
import modelo.Venta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ✅ NUEVO: Panel para que el vendedor vea su historial de ventas
 */
public class MisVentasPanel extends JPanel {

    private final Vendedor vendedor;
    private final ReporteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JLabel lblTotalVentas;
    private final JLabel lblTotalMonto;
    private final JLabel lblComisionEstimada;

    public MisVentasPanel(Vendedor vendedor) {
        this.vendedor = vendedor;
        this.controlador = new ReporteControlador();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== PANEL SUPERIOR (TÍTULO Y ESTADÍSTICAS) =====
        JPanel panelNorte = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel(" Mis Ventas Realizadas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel panelStats = crearPanelEstadisticas();

        panelNorte.add(lblTitulo, BorderLayout.NORTH);
        panelNorte.add(panelStats, BorderLayout.CENTER);

        add(panelNorte, BorderLayout.NORTH);

        // ===== TABLA DE VENTAS =====
        String[] columnas = {"ID Venta", "Fecha", "Cliente", "Vehículo", "Precio Final", "Método Pago"};
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

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ===== PANEL INFERIOR (TOTALES) =====
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelSur.setBackground(new Color(236, 240, 241));
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalVentas = new JLabel("Total Ventas: 0");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblTotalMonto = new JLabel("Monto Total: $0.00");
        lblTotalMonto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalMonto.setForeground(new Color(46, 204, 113));

        lblComisionEstimada = new JLabel("Comisión Estimada: $0.00");
        lblComisionEstimada.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblComisionEstimada.setForeground(new Color(52, 152, 219));

        panelSur.add(lblTotalVentas);
        panelSur.add(new JSeparator(SwingConstants.VERTICAL));
        panelSur.add(lblTotalMonto);
        panelSur.add(new JSeparator(SwingConstants.VERTICAL));
        panelSur.add(lblComisionEstimada);

        add(panelSur, BorderLayout.SOUTH);

        // Cargar datos
        cargarMisVentas();
    }

    /**
     * Crea panel con estadísticas resumidas
     */
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tarjeta 1: Ventas del Mes
        JPanel card1 = crearTarjetaEstadistica(
                "Ventas del Mes",
                "0",
                new Color(52, 152, 219)
        );

        // Tarjeta 2: Ventas del Año
        JPanel card2 = crearTarjetaEstadistica(
                "Ventas del Año",
                "0",
                new Color(46, 204, 113)
        );

        // Tarjeta 3: Comisión Acumulada
        JPanel card3 = crearTarjetaEstadistica(
                "Comisión Acumulada",
                "$0.00",
                new Color(241, 196, 15)
        );

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);

        return panel;
    }

    /**
     * Crea una tarjeta de estadística individual
     */
    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblEmoji = new JLabel(" ", SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        JPanel panelTexto = getJPanel(titulo, valor);

        panel.add(lblEmoji, BorderLayout.WEST);
        panel.add(panelTexto, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel getJPanel(String titulo, String valor) {
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(Color.WHITE);

        JPanel panelTexto = new JPanel(new BorderLayout());
        panelTexto.setOpaque(false);
        panelTexto.add(lblTitulo, BorderLayout.NORTH);
        panelTexto.add(lblValor, BorderLayout.CENTER);
        return panelTexto;
    }

    /**
     * Carga las ventas del vendedor actual
     */
    private void cargarMisVentas() {
        modeloTabla.setRowCount(0);

        SwingWorker<List<Venta>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                // Obtener todas las ventas (filtraremos por vendedor)
                return controlador.listarReporteVentas(null, null);
            }

            @Override
            protected void done() {
                try {
                    List<Venta> todasLasVentas = get();
                    int totalVentas = 0;
                    double montoTotal = 0.0;

                    // Filtrar solo las ventas de este vendedor
                    for (Venta v : todasLasVentas) {
                        if (v.getIdVendedor() == vendedor.getIdVendedor()) {
                            modeloTabla.addRow(new Object[]{
                                    v.getIdVenta(),
                                    v.getFechaVenta(),
                                    v.getNombreCliente(),
                                    v.getNombreVehiculo(),
                                    String.format("$%.2f", v.getPrecioFinal()),
                                    "Contado" // Puedes agregar el método de pago a Venta si lo necesitas
                            });

                            totalVentas++;
                            montoTotal += v.getPrecioFinal();
                        }
                    }

                    // Actualizar totales
                    lblTotalVentas.setText("Total Ventas: " + totalVentas);
                    lblTotalMonto.setText(String.format("Monto Total: $%.2f", montoTotal));

                    // Calcular comisión
                    double comision = montoTotal * (vendedor.getComisionPorcentaje() / 100.0);
                    lblComisionEstimada.setText(String.format("Comisión Estimada: $%.2f", comision));

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MisVentasPanel.this,
                            "Error al cargar ventas: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }
}