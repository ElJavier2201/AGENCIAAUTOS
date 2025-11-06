package vista;

import controlador.ReporteControlador;
import modelo.Venta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para mostrar el reporte de ventas (reemplaza al de la taquería).
 */
public class ReportesPanel extends JPanel {

    private final ReporteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JLabel lblTotalVendido; // Label para mostrar el total

    public ReportesPanel() {
        controlador = new ReporteControlador();
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Reporte General de Ventas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Venta", "Fecha", "Vehículo", "Cliente", "Vendedor", "Precio Final"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);

        cargarReporte();
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones y total
        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefrescar = new JButton("Refrescar Reporte");
        // (Aquí podríamos añadir el botón de PDF si la librería está)
        panelBotones.add(btnRefrescar);

        lblTotalVendido = new JLabel("Total Vendido: $0.00");
        lblTotalVendido.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalVendido.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // Margen

        panelSur.add(panelBotones, BorderLayout.WEST);
        panelSur.add(lblTotalVendido, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);

        // Acciones
        btnRefrescar.addActionListener(e -> cargarReporte());
    }

    private void cargarReporte() {
        modeloTabla.setRowCount(0);
        List<Venta> ventas = controlador.listarReporteVentas();
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
            total += v.getPrecioFinal(); // Sumar al total
        }

        // Actualizar el label del total
        lblTotalVendido.setText(String.format("Total Vendido: $ %.2f", total));
    }
}
