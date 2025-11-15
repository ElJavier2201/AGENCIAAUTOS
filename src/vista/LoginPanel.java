package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.Objects;

/**
 * Ventana de inicio de sesión con diseño moderno
 * --- VISUALMENTE MEJORADO ---
 */
public class LoginPanel extends JFrame {
    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;
    private final JCheckBox chkMostrarPassword;
    private final VendedorControlador vendedorControlador;

    // --- Colores para la UI ---
    private static final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private static final Color COLOR_PRIMARIO_OSCURO = new Color(41, 128, 185);
    private static final Color COLOR_FONDO_IZQUIERDO = new Color(52, 73, 94);
    private static final Color COLOR_TEXTO_PRINCIPAL = new Color(44, 62, 80);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(127, 140, 141);
    private static final Color COLOR_BORDE = new Color(189, 195, 199);
    private static final Color COLOR_FONDO_DERECHO = Color.WHITE;

    public LoginPanel() {
        setTitle("Agencia de Autos - Inicio de Sesión");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2, 0, 0));
        setMinimumSize(new Dimension(800, 500)); // Evita que se colapse

        vendedorControlador = new VendedorControlador();

        // ===== PANEL IZQUIERDO (BRANDING) =====
        JPanel panelIzquierdo = crearPanelBranding();
        add(panelIzquierdo);

        // ===== PANEL DERECHO (FORMULARIO) =====
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(COLOR_FONDO_DERECHO);
        panelDerecho.setLayout(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; // Permitir que los campos crezcan

        // Título
        gbc.gridy++;
        JLabel lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblTitulo, gbc);

        // Subtítulo
        gbc.gridy++;
        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        panelDerecho.add(lblSubtitulo, gbc);

        // Espacio
        gbc.gridy++;
        panelDerecho.add(Box.createVerticalStrut(30), gbc);

        // Campo Usuario
        gbc.gridy++;
        JLabel lblUsuario = new JLabel("👤 Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblUsuario, gbc);

        gbc.gridy++;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // --- MEJORA: Borde inferior estilo "Material Design" ---
        txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
        txtUsuario.setPreferredSize(new Dimension(300, 45));

        // Placeholder effect (mantenemos la lógica que tenías)
        txtUsuario.setText("Ingresa tu usuario");
        txtUsuario.setForeground(Color.GRAY);
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().equals("Ingresa tu usuario")) {
                    txtUsuario.setText("");
                    txtUsuario.setForeground(Color.BLACK);
                    txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_PRIMARIO)); // Borde azul al enfocar
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setText("Ingresa tu usuario");
                    txtUsuario.setForeground(Color.GRAY);
                }
                txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE)); // Borde gris al perder foco
            }
        });
        panelDerecho.add(txtUsuario, gbc);

        // Campo Contraseña
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel lblContrasena = new JLabel("🔒 Contraseña");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContrasena.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblContrasena, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 10, 0);
        txtContrasena = new JPasswordField(20);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // --- MEJORA: Borde inferior estilo "Material Design" ---
        txtContrasena.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
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

        txtContrasena.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtContrasena.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtContrasena.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
            }
        });

        panelDerecho.add(txtContrasena, gbc);

        // Checkbox mostrar contraseña
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 20, 0);
        chkMostrarPassword = new JCheckBox("Mostrar contraseña");
        chkMostrarPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrarPassword.setBackground(COLOR_FONDO_DERECHO);
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
        // --- MEJORA: Usamos nuestro nuevo botón redondeado ---
        btnIngresar = new RoundedButton("Iniciar Sesión", COLOR_PRIMARIO, COLOR_PRIMARIO_OSCURO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setPreferredSize(new Dimension(300, 50));
        btnIngresar.addActionListener(e -> intentarLogin());
        panelDerecho.add(btnIngresar, gbc);

        // Texto de ayuda
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JLabel lblAyuda = new JLabel("<html><center>¿Olvidaste tu contraseña?<br>" +
                "<font color='#3498db'>Contacta al administrador</font></center></html>",
                SwingConstants.CENTER);
        lblAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAyuda.setForeground(COLOR_TEXTO_SECUNDARIO);
        panelDerecho.add(lblAyuda, gbc);

        add(panelDerecho);
    }

    /**
     * Crea el panel de branding (lado izquierdo)
     */
    private JPanel crearPanelBranding() {
        JPanel panel = getJPanel(); // Panel con fondo degradado

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblLogoGrande = new JLabel();
        try {
            // Buscamos la imagen en los recursos
            URL imgUrl = getClass().getResource("/recursos/logo_agencia.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
                lblLogoGrande.setIcon(new ImageIcon(img));
            } else {
                // Si no encuentra la imagen, pone el emoji
                lblLogoGrande.setText(" ");
                lblLogoGrande.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            }
        } catch (Exception e) {
            lblLogoGrande.setText("🚗");
            lblLogoGrande.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        }
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
                " Gestión completa de inventario",
                " Sistema de ventas y facturación",
                " Control de pagos y financiamiento",
                " Reportes detallados"
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
        gbc.weighty = 1.0; // Empuja este componente al fondo
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(40, 0, 20, 0);
        JLabel lblVersion = new JLabel("Versión 1.0.0 - 2024", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblVersion.setForeground(new Color(189, 195, 199));
        panel.add(lblVersion, gbc);

        return panel;
    }

    private static JPanel getJPanel() {
        // --- MEJORA: Gradiente más oscuro ---
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gp = new GradientPaint(
                        0, 0, COLOR_FONDO_IZQUIERDO.brighter(),
                        0, getHeight(), COLOR_FONDO_IZQUIERDO.darker()
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

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Validando...");

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
                        mostrarExito("¡Bienvenido, " + v.getNombre() + "!");
                        Timer timer = new Timer(1000, e -> {
                            abrirPanelPorRol(v);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
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

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Acceso Concedido",
                JOptionPane.INFORMATION_MESSAGE);
    }

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

    // --- CLASE INTERNA PARA BOTÓN REDONDEADO ---
    private static class RoundedButton extends JButton {
        private final Color backgroundColor;
        private final Color hoverColor;

        public RoundedButton(String text, Color background, Color hover) {
            super(text);
            this.backgroundColor = background;
            this.hoverColor = hover;

            setContentAreaFilled(false); // No pintar el fondo por defecto
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(backgroundColor);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Pintar el fondo redondeado
            if (getModel().isArmed()) {
                g2.setColor(hoverColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(backgroundColor);
            }
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

            // Pintar el texto
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}