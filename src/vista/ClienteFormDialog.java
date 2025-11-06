package vista;
import controlador.ClienteControlador;
import modelo.Cliente;
import javax.swing.*;
import java.awt.*;

/**
 * JDialog para agregar o editar un Cliente.
 */
public class ClienteFormDialog extends JDialog {

    private final ClienteControlador controlador;
    private final Cliente cliente; // null si es 'nuevo'
    private boolean guardado = false;

    // Componentes del Formulario
    private JTextField txtNombre, txtApellido, txtTelefono, txtEmail, txtRfc;
    private JTextArea txtDireccion;

    public ClienteFormDialog(Frame owner, Cliente cliente) {
        super(owner, true);
        this.cliente = cliente;
        this.controlador = new ClienteControlador();

        setTitle(cliente == null ? "Agregar Nuevo Cliente" : "Editar Cliente");
        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        if (cliente != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNombre = new JTextField();
        panel.add(txtNombre, gbc);

        // Fila 2: Apellido
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panel.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtApellido = new JTextField();
        panel.add(txtApellido, gbc);

        // Fila 3: Teléfono
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField();
        panel.add(txtTelefono, gbc);

        // Fila 4: Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField();
        panel.add(txtEmail, gbc);

        // Fila 5: RFC
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("RFC:"), gbc);
        gbc.gridx = 1;
        txtRfc = new JTextField();
        panel.add(txtRfc, gbc);

        // Fila 6: Dirección
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.weighty = 1.0;
        txtDireccion = new JTextArea(3, 20);
        panel.add(new JScrollPane(txtDireccion), gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private void precargarDatos() {
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtRfc.setText(cliente.getRfc());
        txtDireccion.setText(cliente.getDireccion());
    }

    private void guardar() {
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Apellido son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente c = (cliente == null) ? new Cliente() : this.cliente;
        c.setNombre(txtNombre.getText());
        c.setApellido(txtApellido.getText());
        c.setTelefono(txtTelefono.getText());
        c.setEmail(txtEmail.getText());
        c.setDireccion(txtDireccion.getText());
        c.setRfc(txtRfc.getText());

        boolean exito;
        if (cliente == null) {
            exito = controlador.agregarCliente(c);
        } else {
            exito = controlador.actualizarCliente(c);
        }

        if (exito) {
            guardado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el cliente (verifique que el email no esté repetido).", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}
