package vista;

import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del Vendedor (Punto de Venta).
 */
public class VendedorPanel extends JFrame {

    private final Vendedor vendedor;
    private final JPanel panelContenido;

    public VendedorPanel(Vendedor vendedor) {
        this.vendedor = vendedor;

        setTitle("Panel de Vendedor - " + vendedor.getNombre());
        setSize(1224, 668);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTitulo = getJPanel(vendedor);
        add(panelTitulo, BorderLayout.NORTH);

        // ===== BARRA LATERAL (MENÚ) =====
        JPanel panelMenu = crearMenuLateral();
        add(panelMenu, BorderLayout.WEST);

        // ===== PANEL DE CONTENIDO =====
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        add(panelContenido, BorderLayout.CENTER);

        // Cargar panel inicial
        mostrarPanelVentas();
    }

    private static JPanel getJPanel(Vendedor vendedor) {
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(22, 160, 133)); // Verde azulado
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Punto de Venta", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel(" " + vendedor.getNombre() + " " + vendedor.getApellido(), SwingConstants.RIGHT);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.WHITE);

        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(lblUsuario, BorderLayout.EAST);
        return panelTitulo;
    }

    /**
     *  NUEVO: Crea el menú lateral con botones grandes
     */
    private JPanel crearMenuLateral() {
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(44, 62, 80)); // Gris azulado oscuro
        panelMenu.setPreferredSize(new Dimension(250, 0));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // ===== BOTONES DEL MENÚ =====

        JButton btnVentas = crearBotonMenu(" Registrar Venta", new Color(26, 188, 156));
        JButton btnCatalogo = crearBotonMenu(" Catálogo Vehículos", new Color(52, 152, 219));
        JButton btnClientes = crearBotonMenu(" Gestión Clientes", new Color(155, 89, 182));
        JButton btnMisVentas = crearBotonMenu(" Mis Ventas", new Color(241, 196, 15));
        JButton btnCerrarSesion = crearBotonMenu(" Cerrar Sesión", new Color(231, 76, 60));

        // Agregar botones al panel
        panelMenu.add(btnVentas);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio
        panelMenu.add(btnCatalogo);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMenu.add(btnClientes);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMenu.add(btnMisVentas);

        // Espacio flexible antes del botón de cerrar sesión
        panelMenu.add(Box.createVerticalGlue());

        // Panel de estadísticas personales
        JPanel panelStats = crearPanelEstadisticas();
        panelMenu.add(panelStats);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 20)));

        panelMenu.add(btnCerrarSesion);

        // ===== ACCIONES DE LOS BOTONES =====

        btnVentas.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnVentas);
            mostrarPanelVentas();
        });

        btnCatalogo.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnCatalogo);
            mostrarPanelCatalogo();
        });

        btnClientes.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnClientes);
            mostrarPanelClientes();
        });

        btnMisVentas.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnMisVentas);
            mostrarPanelMisVentas();
        });

        btnCerrarSesion.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cerrar sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                new LoginPanel().setVisible(true);
                dispose();
            }
        });

        // Marcar botón inicial como activo
        marcarBotonActivo(btnVentas);

        return panelMenu;
    }

    /**
     *  Panel con estadísticas del vendedor
     */
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(127, 140, 141), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(230, 120));

        JLabel lblTitulo = new JLabel(" Mis Estadísticas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVentas = new JLabel("Ventas del mes: --");
        lblVentas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVentas.setForeground(new Color(236, 240, 241));
        lblVentas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblComision = new JLabel("Comisión: $--");
        lblComision.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblComision.setForeground(new Color(236, 240, 241));
        lblComision.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRanking = new JLabel("Ranking: --");
        lblRanking.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRanking.setForeground(new Color(236, 240, 241));
        lblRanking.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblVentas);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblComision);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblRanking);

        return panel;
    }

    /**
     *  Crea un botón estilizado para el menú
     */
    private JButton crearBotonMenu(String texto, Color color) {
        JButton boton = new JButton(texto);

        // Tamaño y alineación
        boton.setPreferredSize(new Dimension(230, 50));
        boton.setMaximumSize(new Dimension(230, 50));
        boton.setMinimumSize(new Dimension(230, 50));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fuente
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Colores
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);

        // Bordes y focus
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Alineación del texto
        boton.setHorizontalAlignment(SwingConstants.LEFT);

        // Efectos hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!boton.getBackground().equals(Color.WHITE)) {
                    boton.setBackground(color.brighter());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!boton.getBackground().equals(Color.WHITE)) {
                    boton.setBackground(color);
                }
            }
        });

        return boton;
    }

    /**
     *: Marca un botón como activo (seleccionado)
     */
    private void marcarBotonActivo(JButton boton) {
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(44, 62, 80));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(26, 188, 156), 3),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    /**
     *  Resetea los estilos de todos los botones
     */
    private void resetearEstilosBotones(JPanel panelMenu) {
        Component[] componentes = panelMenu.getComponents();
        Color[] coloresOriginales = {
                new Color(26, 188, 156),  // Ventas
                new Color(52, 152, 219),  // Catálogo
                new Color(155, 89, 182),  // Clientes
                new Color(241, 196, 15),  // Mis Ventas
                new Color(231, 76, 60)    // Cerrar Sesión
        };

        int colorIndex = 0;
        for (Component comp : componentes) {
            if (comp instanceof JButton btn) {

                // Solo resetear si es uno de los botones principales (no el de cerrar sesión si está al final)
                if (colorIndex < coloresOriginales.length) {
                    Color color = coloresOriginales[colorIndex];

                    btn.setBackground(color);
                    btn.setForeground(Color.WHITE);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(color.darker(), 2),
                            BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    ));

                    colorIndex++;
                }
            }
        }
    }

    // ===== MÉTODOS PARA CAMBIAR PANELES =====

    private void mostrarPanelVentas() {
        panelContenido.removeAll();
        panelContenido.add(new RegistrarVentaPanel(vendedor), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelCatalogo() {
        panelContenido.removeAll();
        panelContenido.add(new VehiculosPanel(false), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelClientes() {
        panelContenido.removeAll();
        panelContenido.add(new ClientesPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarPanelMisVentas() {
        panelContenido.removeAll();
        panelContenido.add(new MisVentasPanel(vendedor), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}