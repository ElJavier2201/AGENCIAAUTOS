package vista;

import com.sun.tools.javac.Main;
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

    public PortalCliente() {
        this.cliente = cliente;

        setTitle("Portal de Cliente - " + cliente.getNombre());
        setSize(1024, 768);
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

        // Pestaña 1: Catálogo (Reusamos el panel)
        tabbedPane.addTab("Ver Catálogo", new VehiculosPanel(false));

        // --- CAMBIO CLAVE ---
        // Ahora usamos el nuevo panel y le pasamos el objeto Cliente
        tabbedPane.addTab("Mi Estado de Cuenta", new ClienteEstadoCuentaPanel(cliente));
        // --- FIN DEL CAMBIO ---

        // Pestaña 3: Mis Compras (Historial)
        // (Este panel seguiría pendiente de crear)
        tabbedPane.addTab("Mis Compras (Próximamente)", new JPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Acción de cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            try {
                Main.main(new String[0]); // Reinicia la aplicación
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
