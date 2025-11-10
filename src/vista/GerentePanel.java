package vista;

import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del Gerente (Administrador).
 */
public class GerentePanel extends JFrame {
    private final JPanel panelContenido;

    public GerentePanel(Vendedor gerente) {

        setTitle("Panel de Gerencia - " + gerente.getNombre());
        setSize(1224, 668);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTitulo = getJPanel(gerente);
        add(panelTitulo, BorderLayout.NORTH);

        // ===== BARRA LATERAL (MENÚ) =====
        JPanel panelMenu = crearMenuLateral();
        add(panelMenu, BorderLayout.WEST);

        // ===== PANEL DE CONTENIDO =====
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        add(panelContenido, BorderLayout.CENTER);

        // Cargar panel inicial
        mostrarPanelVehiculos();
    }

    private static JPanel getJPanel(Vendedor gerente) {
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(41, 128, 185)); // Azul
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Panel de Gerencia", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("👤 " + gerente.getNombre(), SwingConstants.RIGHT);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.WHITE);

        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(lblUsuario, BorderLayout.EAST);
        return panelTitulo;
    }

    /**
     * NUEVO: Crea el menú lateral con botones grandes
     */
    private JPanel crearMenuLateral() {
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(52, 73, 94)); // Gris azulado oscuro
        panelMenu.setPreferredSize(new Dimension(250, 0));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // ===== BOTONES DEL MENÚ =====

        JButton btnVehiculos = crearBotonMenu(" Inventario Vehículos", new Color(52, 152, 219));
        JButton btnVendedores = crearBotonMenu(" Gestión Vendedores", new Color(46, 204, 113));
        JButton btnClientes = crearBotonMenu(" Gestión Clientes", new Color(155, 89, 182));
        JButton btnReportes = crearBotonMenu(" Reporte de Ventas", new Color(230, 126, 34));
        JButton btnPagos = crearBotonMenu("Gestión de Pagos", new Color(241, 196, 15));
        JButton btnCerrarSesion = crearBotonMenu(" Cerrar Sesión", new Color(231, 76, 60));

        // Agregar botones al panel
        panelMenu.add(btnVehiculos);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio
        panelMenu.add(btnVendedores);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMenu.add(btnClientes);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMenu.add(btnReportes);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        panelMenu.add(btnPagos);

        // Espacio flexible antes del botón de cerrar sesión
        panelMenu.add(Box.createVerticalGlue());

        panelMenu.add(btnCerrarSesion);

        // ===== ACCIONES DE LOS BOTONES =====

        btnVehiculos.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnVehiculos);
            mostrarPanelVehiculos();
        });

        btnVendedores.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnVendedores);
            mostrarPanelVendedores();
        });

        btnClientes.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnClientes);
            mostrarPanelClientes();
        });

        btnReportes.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnReportes);
            mostrarPanelReportes();
        });

        btnPagos.addActionListener(e -> {
            resetearEstilosBotones(panelMenu);
            marcarBotonActivo(btnPagos);
            mostrarPanelPagos();
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
        marcarBotonActivo(btnVehiculos);

        return panelMenu;
    }

    /**
     * : Crea un botón estilizado para el menú
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
     * NUEVO: Marca un botón como activo (seleccionado)
     */
    private void marcarBotonActivo(JButton boton) {
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(52, 73, 94));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 3),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    /**
     *  NUEVO: Resetea los estilos de todos los botones
     */
    private void resetearEstilosBotones(JPanel panelMenu) {
        Component[] componentes = panelMenu.getComponents();
        Color[] coloresOriginales = {
                new Color(52, 152, 219),  // Vehículos
                new Color(46, 204, 113),  // Vendedores
                new Color(155, 89, 182),  // Clientes
                new Color(230, 126, 34),  // Reportes
                new Color(241, 196, 15),  // Pagos
                new Color(231, 76, 60)    // Cerrar Sesión
        };

        int colorIndex = 0;
        for (Component comp : componentes) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                Color color = coloresOriginales[colorIndex % coloresOriginales.length];

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

    // ===== MÉTODOS PARA CAMBIAR PANELES =====

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

    private void mostrarPanelPagos() {
        panelContenido.removeAll();
        panelContenido.add(new EstadoCuentaPanel(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}