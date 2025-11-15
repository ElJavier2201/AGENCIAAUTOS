package vista;

import modelo.Cliente;
import javax.swing.*;
import java.awt.*;

/**
 * JFrame (Ventana) para el "Portal del Cliente".
 * Muestra el catálogo de vehículos en modo de solo lectura.
 */
public class PortalCliente extends JFrame {

    public PortalCliente(Cliente cliente) { // Permitir que cliente sea NULL

        setTitle("Portal de Cliente - " + cliente.getNombre());
        setSize(924, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de bienvenida
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBienvenida = new JLabel();
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        JButton btnIniciarSesion = new JButton("Iniciar Sesión");

        panelNorte.add(lblBienvenida);
        panelNorte.add(btnIniciarSesion);
        panelNorte.add(btnCerrarSesion);
        add(panelNorte, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ver Catálogo", new VehiculosPanel(false));

        if (cliente != null) {
            tabbedPane.addTab("Mi Estado de Cuenta", new ClienteEstadoCuentaPanel(cliente));
            tabbedPane.addTab("Mis Compras (Próximamente)", new JPanel());

            // Configurar bienvenida y botones
            lblBienvenida.setText("Bienvenido, " + cliente.getNombre() + " " + cliente.getApellido());
            btnIniciarSesion.setVisible(false);
            btnCerrarSesion.setVisible(true);
        } else {
            // --- MODO INVITADO ---
            lblBienvenida.setText("Bienvenido, Invitado.");
            btnIniciarSesion.setVisible(true);
            btnCerrarSesion.setVisible(false);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Acción de cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
        });

        btnIniciarSesion.addActionListener(e -> {
            ClienteLoginDialog loginDialog = new ClienteLoginDialog(this);
            loginDialog.setVisible(true);

            Cliente clienteAutenticado = loginDialog.getClienteAutenticado();
            if (clienteAutenticado != null) {
                // Login exitoso: cierra este portal de invitado y abre uno nuevo
                dispose();
                new PortalCliente(clienteAutenticado).setVisible(true);
            }
            // Si no hizo login, no hace nada y sigue como invitado
        });
    }
}
