package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de inicio de sesión con diseño moderno
 */
public class LoginPanel extends JFrame {
    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;
    private final JCheckBox chkMostrarPassword;
    private final VendedorControlador vendedorControlador;

    public LoginPanel() {
        setTitle("Agencia de Autos - Inicio de Sesión");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2, 0, 0));

        vendedorControlador = new VendedorControlador();

        // ===== PANEL IZQUIERDO (BRANDING) =====
        JPanel panelIzquierdo = crearPanelBranding();
        add(panelIzquierdo);

        // ===== PANEL DERECHO (FORMULARIO) =====
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setLayout(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo pequeño arriba
        JLabel lblIcono = new JLabel("🚗", SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        panelDerecho.add(lblIcono, gbc);

        // Título
        gbc.gridy++;
        JLabel lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(44, 62, 80));
        panelDerecho.add(lblTitulo, gbc);

        // Subtítulo
        gbc.gridy++;
        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(127, 140, 141));
        panelDerecho.add(lblSubtitulo, gbc);

        // Espacio
        gbc.gridy++;
        panelDerecho.add(Box.createVerticalStrut(30), gbc);

        // Campo Usuario
        gbc.gridy++;
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(new Color(52, 73, 94));
        panelDerecho.add(lblUsuario, gbc);

        gbc.gridy++;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtUsuario.setPreferredSize(new Dimension(300, 45));

        // Placeholder effect
        txtUsuario.setText("Ingresa tu usuario");
        txtUsuario.setForeground(Color.GRAY);
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().equals("Ingresa tu usuario")) {
                    txtUsuario.setText("");
                    txtUsuario.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setText("Ingresa tu usuario");
                    txtUsuario.setForeground(Color.GRAY);
                }
            }
        });

        panelDerecho.add(txtUsuario, gbc);

        // Campo Contraseña
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContrasena.setForeground(new Color(52, 73, 94));
        panelDerecho.add(lblContrasena, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 10, 0);
        txtContrasena = new JPasswordField(20);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtContrasena.setPreferredSize(new Dimension(300, 45));
        txtContrasena.setEchoChar('●');

        // Enter para login
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });

        panelDerecho.add(txtContrasena, gbc);

        // Checkbox mostrar contraseña
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 20, 0);
        chkMostrarPassword = new JCheckBox("Mostrar contraseña");
        chkMostrarPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrarPassword.setBackground(Color.WHITE);
        chkMostrarPassword.setFocusPainted(false);
        chkMostrarPassword.addActionListener(e -> {
            if (chkMostrarPassword.isSelected()) {
                txtContrasena.setEchoChar((char) 0);
            } else {
                txtContrasena.setEchoChar('●');
            }
        });
        panelDerecho.add(chkMostrarPassword, gbc);

        // Botón Ingresar
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 20, 0);
        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnIngresar.setBackground(new Color(52, 152, 219));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setPreferredSize(new Dimension(300, 50));
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnIngresar.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnIngresar.setBackground(new Color(52, 152, 219));
            }
        });

        btnIngresar.addActionListener(e -> intentarLogin());
        panelDerecho.add(btnIngresar, gbc);

        // Texto de ayuda
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JLabel lblAyuda = new JLabel("<html><center>¿Olvidaste tu contraseña?<br>" +
                "<font color='#3498db'>Contacta al administrador</font></center></html>",
                SwingConstants.CENTER);
        lblAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(127, 140, 141));
        panelDerecho.add(lblAyuda, gbc);

        add(panelDerecho);
    }

    /**
     * Crea el panel de branding (lado izquierdo)
     */
    private JPanel crearPanelBranding() {
        JPanel panel = getJPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);

        // Logo grande
        JLabel lblLogoGrande = new JLabel(" ", SwingConstants.CENTER);
        lblLogoGrande.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        panel.add(lblLogoGrande, gbc);

        // Título de la empresa
        gbc.gridy++;
        JLabel lblEmpresa = new JLabel("Agencia de Autos", SwingConstants.CENTER);
        lblEmpresa.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblEmpresa.setForeground(Color.WHITE);
        panel.add(lblEmpresa, gbc);

        // Slogan
        gbc.gridy++;
        JLabel lblSlogan = new JLabel("Tu mejor opción en vehículos", SwingConstants.CENTER);
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSlogan.setForeground(new Color(236, 240, 241));
        panel.add(lblSlogan, gbc);

        // Características
        gbc.gridy++;
        gbc.insets = new Insets(40, 0, 10, 0);
        JPanel panelCaracteristicas = new JPanel();
        panelCaracteristicas.setLayout(new BoxLayout(panelCaracteristicas, BoxLayout.Y_AXIS));
        panelCaracteristicas.setOpaque(false);

        String[] caracteristicas = {
                "✓ Gestión completa de inventario",
                "✓ Sistema de ventas y facturación",
                "✓ Control de pagos y financiamiento",
                "✓ Reportes detallados"
        };

        for (String car : caracteristicas) {
            JLabel lblCar = new JLabel(car);
            lblCar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblCar.setForeground(Color.WHITE);
            lblCar.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            panelCaracteristicas.add(lblCar);
        }

        panel.add(panelCaracteristicas, gbc);

        // Versión
        gbc.gridy++;
        gbc.insets = new Insets(40, 0, 20, 0);
        JLabel lblVersion = new JLabel("Versión 1.0.0 - 2024", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblVersion.setForeground(new Color(189, 195, 199));
        panel.add(lblVersion, gbc);

        return panel;
    }

    private static JPanel getJPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Gradiente de fondo
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradiente azul
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(52, 152, 219),
                        0, getHeight(), new Color(41, 128, 185)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    /**
     * Intenta hacer login
     */
    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());

        // Validaciones básicas
        if (usuario.isEmpty() || usuario.equals("Ingresa tu usuario")) {
            mostrarError("Por favor ingresa tu usuario");
            txtUsuario.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            mostrarError("Por favor ingresa tu contraseña");
            txtContrasena.requestFocus();
            return;
        }

        // Deshabilitar botón mientras valida
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Validando...");

        // Simular carga (en producción esto sería async)
        SwingWorker<Vendedor, Void> worker = new SwingWorker<>() {
            @Override
            protected Vendedor doInBackground() throws Exception {
                return vendedorControlador.login(usuario, pass);
            }

            @Override
            protected void done() {
                try {
                    Vendedor v = get();

                    if (v != null) {
                        // Login exitoso
                        mostrarExito("¡Bienvenido, " + v.getNombre() + "!");

                        // Esperar un momento para que vea el mensaje
                        Timer timer = new Timer(1000, e -> {
                            abrirPanelPorRol(v);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        // Login fallido
                        mostrarError("Usuario o contraseña incorrectos");
                        btnIngresar.setEnabled(true);
                        btnIngresar.setText("Iniciar Sesión");
                        txtContrasena.setText("");
                        txtContrasena.requestFocus();
                    }
                } catch (Exception e) {
                    mostrarError("Error al conectar: " + e.getMessage());
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("Iniciar Sesión");
                }
            }
        };

        worker.execute();
    }

    /**
     * Muestra un mensaje de error con estilo
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Acceso Concedido",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Abre el panel correspondiente según el rol del vendedor
     */
    private void abrirPanelPorRol(Vendedor vendedor) {
        switch (vendedor.getRol().toLowerCase()) {
            case "gerente":
                new GerentePanel(vendedor).setVisible(true);
                break;
            case "vendedor":
                new VendedorPanel(vendedor).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "Rol no reconocido: " + vendedor.getRol(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }
}