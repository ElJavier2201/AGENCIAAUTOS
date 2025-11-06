package vista;

import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;
import vista.VendedoresPanel;
import vista.VehiculosPanel;
import vista.ClientesPanel;
import vista.ReportesPanel;

/**
 * Panel principal del Gerente (Administrador).
 */
public class GerentePanel extends JFrame {
    private final Vendedor gerente;
    private final JPanel panelContenido;

    public GerentePanel(Vendedor gerente) {
        this.gerente = gerente;

        setTitle("Panel de Gerencia - " + gerente.getNombre());
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Panel de Gerencia: " + gerente.getNombre(), SwingConstants.CENTER);
        // ... (código del título)
        add(lblTitulo, BorderLayout.NORTH);

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);

        JButton btnVehiculos = new JButton(" Inventario Vehículos ");
        JButton btnVendedores = new JButton(" Gestión Vendedores ");
        JButton btnClientes = new JButton(" Gestión Clientes ");
        JButton btnReportes = new JButton(" Reporte de Ventas ");
        JButton btnCerrarSesion = new JButton(" Cerrar Sesión ");

        toolBar.add(btnVehiculos);
        toolBar.add(btnVendedores);
        toolBar.add(btnClientes);
        toolBar.add(btnReportes);
        toolBar.add(Box.createVerticalGlue());
        toolBar.add(btnCerrarSesion);

        add(toolBar, BorderLayout.WEST);

        panelContenido = new JPanel(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);

        btnVehiculos.addActionListener(e -> mostrarPanelVehiculos());
        btnVendedores.addActionListener(e -> mostrarPanelVendedores());
        btnClientes.addActionListener(e -> mostrarPanelClientes());
        btnReportes.addActionListener(e -> mostrarPanelReportes());

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
    // ----------------------------
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
}