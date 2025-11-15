package vista;

import controlador.ClienteControlador;
import modelo.Cliente;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * --- VISUALMENTE MEJORADO (Estilo LoginPanel) ---
 * Dialog para que los clientes se identifiquen con usuario y contraseña.
 */
public class ClienteLoginDialog extends JDialog {

    private final ClienteControlador controlador;
    private Cliente clienteAutenticado = null;

    // --- Componentes del formulario ---
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JCheckBox chkMostrarPassword;

    // --- Paleta de colores (consistente con LoginPanel) ---
    private static final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private static final Color COLOR_PRIMARIO_OSCURO = new Color(41, 128, 185);
    private static final Color COLOR_FONDO_IZQUIERDO = new Color(52, 73, 94);
    private static final Color COLOR_TEXTO_PRINCIPAL = new Color(44, 62, 80);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(127, 140, 141);
    private static final Color COLOR_BORDE = new Color(189, 195, 199);
    private static final Color COLOR_FONDO_DERECHO = Color.WHITE;

    public ClienteLoginDialog(Frame owner) {
        super(owner, "Identificación de Cliente", true);
        this.controlador = new ClienteControlador();

        setTitle("Portal de Cliente");
        setSize(800, 450); // Tamaño para 2 columnas
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(1, 2, 0, 0)); // Layout principal de 2 columnas

        // ===== 1. PANEL IZQUIERDO (BRANDING) =====
        add(crearPanelBranding());

        // ===== 2. PANEL DERECHO (FORMULARIO) =====
        add(crearPanelFormulario());

        // Listener para la tecla Enter
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ingresar();
                }
            }
        };
        txtUsuario.addKeyListener(enterListener);
        txtContrasena.addKeyListener(enterListener);
    }

    /**
     * Crea el panel de branding (lado izquierdo)
     */
    private JPanel crearPanelBranding() {
        JPanel panel = getPanelConGradiente(); // Panel con fondo degradado

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblIcono = new JLabel(" ");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        lblIcono.setForeground(Color.WHITE);
        panel.add(lblIcono, gbc);

        // Título
        gbc.gridy++;
        JLabel lblEmpresa = new JLabel("Portal de Cliente", SwingConstants.CENTER);
        lblEmpresa.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblEmpresa.setForeground(Color.WHITE);
        panel.add(lblEmpresa, gbc);

        // Slogan
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 20, 0);
        JLabel lblSlogan = new JLabel("Consulta tus vehículos y financiamientos", SwingConstants.CENTER);
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSlogan.setForeground(new Color(236, 240, 241));
        panel.add(lblSlogan, gbc);

        return panel;
    }

    /**
     * Crea el panel del formulario (lado derecho)
     */
    private JPanel crearPanelFormulario() {
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(COLOR_FONDO_DERECHO);
        panelDerecho.setLayout(new GridBagLayout());
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // Título
        gbc.gridy++;
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblTitulo, gbc);

        // Espacio
        gbc.gridy++;
        panelDerecho.add(Box.createVerticalStrut(20), gbc);

        // --- Usuario ---
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblUsuario, gbc);

        gbc.gridy++;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
        panelDerecho.add(txtUsuario, gbc);

        // --- Contraseña ---
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 5, 0);
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContrasena.setForeground(COLOR_TEXTO_PRINCIPAL);
        panelDerecho.add(lblContrasena, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        txtContrasena = new JPasswordField(20);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtContrasena.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
        txtContrasena.setEchoChar('●');
        panelDerecho.add(txtContrasena, gbc);

        // --- Checkbox ---
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 15, 0);
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

        // --- Panel de Botones ---
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false); // Transparente

        // Botón Cancelar (Estilo Outline)
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBackground(COLOR_FONDO_DERECHO);
        btnCancelar.setForeground(COLOR_TEXTO_SECUNDARIO);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 2, true));
        btnCancelar.setPreferredSize(new Dimension(140, 45));
        btnCancelar.addActionListener(e -> dispose());

        // Botón Ingresar (Estilo Sólido)
        btnIngresar = new RoundedButton("Ingresar", COLOR_PRIMARIO, COLOR_PRIMARIO_OSCURO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setPreferredSize(new Dimension(140, 45));
        btnIngresar.addActionListener(e -> ingresar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnIngresar);
        panelDerecho.add(panelBotones, gbc);

        // --- Listeners de Foco ---
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUsuario.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_BORDE));
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

        return panelDerecho;
    }

    /**
     * Método de ayuda para el fondo degradado del panel izquierdo
     */
    private JPanel getPanelConGradiente() {
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

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar usuario y contraseña.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Validando...");
        btnCancelar.setEnabled(false);

        SwingWorker<Cliente, Void> worker = new SwingWorker<>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return controlador.autenticar(usuario, pass);
            }

            @Override
            protected void done() {
                try {
                    clienteAutenticado = get();
                    if (clienteAutenticado != null) {
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ClienteLoginDialog.this,
                                "Usuario o contraseña incorrectos.",
                                "Error de Autenticación",
                                JOptionPane.ERROR_MESSAGE);
                        btnIngresar.setEnabled(true);
                        btnIngresar.setText("Ingresar");
                        btnCancelar.setEnabled(true);
                        txtContrasena.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClienteLoginDialog.this,
                            "Error al conectar con la base de datos.",
                            "Error de Conexión",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    btnIngresar.setEnabled(true);
                    btnIngresar.setText("Ingresar");
                    btnCancelar.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    public Cliente getClienteAutenticado() {
        return clienteAutenticado;
    }

    // --- CLASE INTERNA PARA BOTÓN REDONDEADO ---
    private static class RoundedButton extends JButton {
        private final Color backgroundColor;
        private final Color hoverColor;

        public RoundedButton(String text, Color background, Color hover) {
            super(text);
            this.backgroundColor = background;
            this.hoverColor = hover;

            setContentAreaFilled(false);
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

            if (getModel().isArmed()) {
                g2.setColor(hoverColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(backgroundColor);
            }
            // Bordes más redondeados
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 40, 40));

            super.paintComponent(g2);
            g2.dispose();
        }
    }
}