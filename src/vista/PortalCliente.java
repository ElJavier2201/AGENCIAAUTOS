package vista;

import modelo.Cliente;
import javax.swing.*;
import java.awt.*;

/**
 * JFrame (Ventana) para el "Portal del Cliente".
 * Muestra el catálogo de vehículos en modo de solo lectura.
 */
public class PortalCliente extends JFrame {

    private final Cliente cliente;
    private JTabbedPane tabbedPane;

    public PortalCliente(Cliente cliente) {// ✅ CORRECCIÓN: Validación de seguridad
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        this.cliente = cliente;

        setTitle("Portal de Cliente - " + cliente.getNombre());
        setSize(924, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de bienvenida
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.add(new JLabel("Bienvenido, " + cliente.getNombre() + " " + cliente.getApellido()));
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        panelNorte.add(btnCerrarSesion);
        add(panelNorte, BorderLayout.NORTH);

        // Pestañas
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ver Catálogo", new VehiculosPanel(false));

        // Pestaña 2: Estado de Cuenta
        tabbedPane.addTab("Mi Estado de Cuenta", new ClienteEstadoCuentaPanel(cliente));

        // Pestaña 3: Mis Compras (Historial)
        tabbedPane.addTab("Mis Compras (Próximamente)", new JPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Acción de cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginPanel().setVisible(true);
            });
        });
    }
}