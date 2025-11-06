package vista;
// CAMBIO: Importar Vendedor y VendedorControlador
import controlador.VendedorControlador;
import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// (Ya no necesitamos la URL de la imagen del taco)

/**
 * Ventana de inicio de sesión (Actualizada para Agencia de Autos).
 */
public class LoginPanel extends JFrame {
    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final JButton btnIngresar;
    // CAMBIO: Usar VendedorControlador
    private final VendedorControlador vendedorControlador;

    public LoginPanel() {
        setTitle("Login - Agencia de Autos"); // CAMBIO: Título
        setSize(400, 300); // CAMBIO: Tamaño (ya no hay logo grande)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // CAMBIO: Usar VendedorControlador
        vendedorControlador = new VendedorControlador();

        // Panel del Título (reemplaza el logo)
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Sistema de Agencia");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel de login (mismo layout, pero con fuentes más grandes)
        JPanel panelLogin = new JPanel(new GridLayout(5, 1, 10, 10));
        panelLogin.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();
        btnIngresar = new JButton("Ingresar");

        Font fuenteCampos = new Font("Arial", Font.PLAIN, 16);
        txtUsuario.setFont(fuenteCampos);
        txtContrasena.setFont(fuenteCampos);

        panelLogin.add(new JLabel("Usuario:"));
        panelLogin.add(txtUsuario);
        panelLogin.add(new JLabel("Contraseña:"));
        panelLogin.add(txtContrasena);
        panelLogin.add(btnIngresar);

        add(panelLogin, BorderLayout.CENTER);

        // Evento del botón
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText().trim();
                String pass = new String(txtContrasena.getPassword());

                // CAMBIO: Llamar al VendedorControlador
                Vendedor v = vendedorControlador.login(usuario, pass);

                if (v != null) {
                    abrirPanelPorRol(v); // CAMBIO: Pasa el objeto Vendedor
                    dispose(); // Cierra el login
                } else {
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Abre el panel correspondiente según el rol del Vendedor autenticado.
     */
    private void abrirPanelPorRol(Vendedor vendedor) {
        switch (vendedor.getRol().toLowerCase()) {

            case "gerente":
                new GerentePanel(vendedor).setVisible(true);
                break;

            case "vendedor": // El rol de 'ricardo'
                new VendedorPanel(vendedor).setVisible(true);
                JOptionPane.showMessageDialog(null, "Panel 'Vendedor' no implementado. Logueado como: " + vendedor.getNombre());
                break;

            default:
                JOptionPane.showMessageDialog(null, "Rol no reconocido: " + vendedor.getRol());
        }
    }
}