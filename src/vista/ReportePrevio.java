
package vista;

import modelo.Venta;
import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 👁️ Vista previa del reporte antes de exportar
 */
public class ReportePrevio extends JDialog {

    private final List<Venta> ventas;

    public ReportePrevio(Frame owner, List<Venta> ventas) {
        super(owner, "Vista Previa del Reporte", true);
        this.ventas = ventas;

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Panel de resumen
        add(crearPanelResumen(), BorderLayout.NORTH);

        // Tabla de datos
        add(crearPanelTabla(), BorderLayout.CENTER);

        // Botones
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        // Calcular métricas
        int totalVentas = ventas.size();
        double totalVendido = ventas.stream()
                .mapToDouble(Venta::getPrecioFinal)
                .sum();
        double ticketPromedio = totalVentas > 0 ? totalVendido / totalVentas : 0;
        double ventaMasAlta = ventas.stream()
                .mapToDouble(Venta::getPrecioFinal)
                .max()
                .orElse(0);

        panel.add(crearTarjetaKPI("📊 Total Ventas",
                String.valueOf(totalVentas),
                new Color(52, 152, 219)));

        panel.add(crearTarjetaKPI("💰 Total Vendido",
                String.format("$%.2f", totalVendido),
                new Color(46, 204, 113)));

        panel.add(crearTarjetaKPI("📈 Ticket Promedio",
                String.format("$%.2f", ticketPromedio),
                new Color(241, 196, 15)));

        panel.add(crearTarjetaKPI("🏆 Venta Más Alta",
                String.format("$%.2f", ventaMasAlta),
                new Color(231, 76, 60)));

        return panel;
    }

    private JPanel crearTarjetaKPI(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(color);

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(color.darker());

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID", "Fecha", "Vehículo", "Cliente", "Vendedor", "Precio"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(52, 152, 219));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Llenar datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Venta v : ventas) {
            modelo.addRow(new Object[]{
                    v.getIdVenta(),
                    sdf.format(v.getFechaVenta()),
                    v.getNombreVehiculo(),
                    v.getNombreCliente(),
                    v.getNombreVendedor(),
                    String.format("$%.2f", v.getPrecioFinal())
            });
        }

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1)
        ));

        return scrollPane;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(236, 240, 241));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setBackground(new Color(149, 165, 166));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnCerrar);

        return panel;
    }
}
