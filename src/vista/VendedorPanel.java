
package vista;

import modelo.Vendedor;
import javax.swing.*;
        import java.awt.*;

/**
 * Panel principal del Vendedor (Punto de Venta).
 */
public class VendedorPanel extends JFrame {

    private Vendedor vendedor;
    private JTabbedPane tabbedPane;

    public VendedorPanel(Vendedor vendedor) {
        this.vendedor = vendedor;

        setTitle("Panel de Vendedor - " + vendedor.getNombre());
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Panel de bienvenida
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.add(new JLabel("Bienvenido, " + vendedor.getNombre() + " " + vendedor.getApellido()));
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        panelNorte.add(btnCerrarSesion);
        add(panelNorte, BorderLayout.NORTH);

        // Crear Pestañas
        tabbedPane = new JTabbedPane();

        // Pestaña 1: Registrar Venta (El formulario)
        // (Crearemos 'RegistrarVentaPanel' a continuación)
         tabbedPane.add("Registrar Venta", new RegistrarVentaPanel(vendedor));

        // Pestaña 2: Catálogo (Reusamos el panel del Gerente)
        // (Le pasamos 'false' para deshabilitar los botones de admin)
         VehiculosPanel panelVehiculos = new VehiculosPanel(false);
         tabbedPane.add("Catálogo Vehículos", panelVehiculos);

        // Pestaña 3: Clientes (Reusamos el panel del Gerente)
        tabbedPane.add("Gestión Clientes", new ClientesPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Acción
        btnCerrarSesion.addActionListener(e -> {
            new LoginPanel().setVisible(true);
            dispose();
        });
    }
}
