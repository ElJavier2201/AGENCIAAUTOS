package vista;

import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;
// (No es necesario importar los otros paneles, ya que están en el mismo paquete)

/**
 * Panel principal del Gerente (Administrador).
 * (Actualizado para incluir Panel de Pagos)
 */
public class GerentePanel extends JFrame {
    private final JPanel panelContenido;

    public GerentePanel(Vendedor gerente) {

        setTitle("Panel de Gerencia - " + gerente.getNombre());
        setSize(824, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Panel de Gerencia: " + gerente.getNombre(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);

        JButton btnVehiculos = new JButton(" Inventario Vehículos ");
        JButton btnVendedores = new JButton(" Gestión Vendedores ");
        JButton btnClientes = new JButton(" Gestión Clientes ");
        JButton btnReportes = new JButton(" Reporte de Ventas ");
        // --- NUEVO BOTÓN ---
        JButton btnPagos = new JButton(" Gestión de Pagos ");
        JButton btnCerrarSesion = new JButton(" Cerrar Sesión ");

        toolBar.add(btnVehiculos);
        toolBar.add(btnVendedores);
        toolBar.add(btnClientes);
        toolBar.add(btnReportes);
        // --- NUEVO ---
        toolBar.add(btnPagos);

        toolBar.add(Box.createVerticalGlue());
        toolBar.add(btnCerrarSesion);

        add(toolBar, BorderLayout.WEST);

        panelContenido = new JPanel(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);

        // Acciones
        btnVehiculos.addActionListener(e -> mostrarPanelVehiculos());
        btnVendedores.addActionListener(e -> mostrarPanelVendedores());
        btnClientes.addActionListener(e -> mostrarPanelClientes());
        btnReportes.addActionListener(e -> mostrarPanelReportes());
        // --- NUEVA ACCIÓN ---
        btnPagos.addActionListener(e -> mostrarPanelPagos());

        btnCerrarSesion.addActionListener(e -> {
            new LoginPanel().setVisible(true);
            dispose();
        });

        mostrarPanelVehiculos(); // Carga inicial
    }

    private void mostrarPanelVehiculos() {
        panelContenido.removeAll();
        panelContenido.add(new VehiculosPanel(true), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelVendedores() {
        panelContenido.removeAll();
        panelContenido.add(new VendedoresPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelClientes() {
        panelContenido.removeAll();
        panelContenido.add(new ClientesPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelReportes() {
        panelContenido.removeAll();
        panelContenido.add(new ReportesPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * --- NUEVO MÉTODO ---
     * Muestra el panel de gestión de estados de cuenta y pagos.
     */
    private void mostrarPanelPagos() {
        panelContenido.removeAll();
        panelContenido.add(new EstadoCuentaPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}