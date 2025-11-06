
package vista;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame (Ventana) para el "Portal del Cliente".
 * Muestra el catálogo de vehículos en modo de solo lectura.
 */
public class PortalCliente extends JFrame {

    public PortalCliente() {
        setTitle("Catálogo de Vehículos - Agencia de Autos");
        setSize(1024, 768); // Un tamaño estándar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Título Superior
        JLabel lblTitulo = new JLabel("Nuestros Vehículos Disponibles", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // 2. Panel de Catálogo (Reutilizado)
        // (Usamos el 'VehiculosPanel' que ya existe, pasándole 'false'
        // para ocultar los botones de Gerente)
        VehiculosPanel panelCatalogo = new VehiculosPanel(false);
        add(panelCatalogo, BorderLayout.CENTER);

        // 3. Pie de Página
        JLabel lblFooter = new JLabel("Para precios y financiamiento, visite nuestra sucursal.", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 12));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblFooter, BorderLayout.SOUTH);
    }
}
