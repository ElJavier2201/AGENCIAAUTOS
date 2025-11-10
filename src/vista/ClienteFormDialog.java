package vista;

import controlador.ClienteControlador;
import modelo.Cliente;
import util.ValidadorSwing;
import util.Validador;
import javax.swing.*;
import java.awt.*;

/**
 * JDialog para agregar o editar un Cliente.
 * ACTUALIZADO: Ahora incluye validaciones en tiempo real
 */
public class ClienteFormDialog extends JDialog {

    private final ClienteControlador controlador;
    private final Cliente cliente;
    private boolean guardado = false;

    private ValidadorSwing txtNombre, txtApellido, txtTelefono, txtEmail, txtRfc;
    private JTextArea txtDireccion;

    // Labels para errores
    private JLabel lblErrorNombre, lblErrorApellido, lblErrorTelefono, lblErrorEmail, lblErrorRfc;

    public ClienteFormDialog(Frame owner, Cliente cliente) {
        super(owner, true);
        this.cliente = cliente;
        this.controlador = new ClienteControlador();

        setTitle(cliente == null ? "Agregar Nuevo Cliente" : "Editar Cliente");
        setSize(500, 500);
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
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ===== NOMBRE =====
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        panel.add(new JLabel("Nombre: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNombre = new ValidadorSwing(20);
        txtNombre.setValidador(texto -> Validador.validarNombre(texto, "nombre"));
        panel.add(txtNombre, gbc);

        // Label de error
        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorNombre = new JLabel();
        lblErrorNombre.setFont(new Font("Arial", Font.ITALIC, 10));
        txtNombre.setLabelError(lblErrorNombre);
        panel.add(lblErrorNombre, gbc);

        // ===== APELLIDO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Apellido: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtApellido = new ValidadorSwing(20);
        txtApellido.setValidador(texto -> Validador.validarNombre(texto, "apellido"));
        panel.add(txtApellido, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorApellido = new JLabel();
        lblErrorApellido.setFont(new Font("Arial", Font.ITALIC, 10));
        txtApellido.setLabelError(lblErrorApellido);
        panel.add(lblErrorApellido, gbc);

        // ===== TELÉFONO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Teléfono: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTelefono = new ValidadorSwing(15);
        txtTelefono.setValidador(texto -> Validador.validarTelefono(texto, true));
        txtTelefono.setToolTipText("10 dígitos. Ej: 2221234567");
        panel.add(txtTelefono, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorTelefono = new JLabel();
        lblErrorTelefono.setFont(new Font("Arial", Font.ITALIC, 10));
        txtTelefono.setLabelError(lblErrorTelefono);
        panel.add(lblErrorTelefono, gbc);

        // ===== EMAIL =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Email: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail = new ValidadorSwing(25);
        txtEmail.setValidador(Validador::validarEmail);
        txtEmail.setToolTipText("Ej: cliente@dominio.com");
        panel.add(txtEmail, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorEmail = new JLabel();
        lblErrorEmail.setFont(new Font("Arial", Font.ITALIC, 10));
        txtEmail.setLabelError(lblErrorEmail);
        panel.add(lblErrorEmail, gbc);

        // ===== RFC =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("RFC:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRfc = new ValidadorSwing(13);
        txtRfc.setValidador(texto -> Validador.validarRfc(texto, false));
        txtRfc.setObligatorio(false);
        txtRfc.setToolTipText("12 o 13 caracteres. Ej: XAXX010101000");
        panel.add(txtRfc, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorRfc = new JLabel();
        lblErrorRfc.setFont(new Font("Arial", Font.ITALIC, 10));
        txtRfc.setLabelError(lblErrorRfc);
        panel.add(lblErrorRfc, gbc);

        // ===== DIRECCIÓN =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Dirección:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        txtDireccion.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(new JScrollPane(txtDireccion), gbc);

        // Nota de campos obligatorios
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0.0;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 10));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

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
        boolean valido = true;

        valido &= txtNombre.validar();
        valido &= txtApellido.validar();
        valido &= txtTelefono.validar();
        valido &= txtEmail.validar();
        valido &= txtRfc.validar();

        if (!valido) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los errores marcados en rojo antes de guardar.",
                    "Errores de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = (cliente == null) ? new Cliente() : this.cliente;

        c.setNombre(Validador.normalizarNombre(txtNombre.getTextoNormalizado()));
        c.setApellido(Validador.normalizarNombre(txtApellido.getTextoNormalizado()));
        c.setTelefono(Validador.normalizarTelefono(txtTelefono.getTextoNormalizado()));
        c.setEmail(txtEmail.getTextoNormalizado().toLowerCase()); // Email en minúsculas
        c.setDireccion(txtDireccion.getText().trim());

        String rfc = txtRfc.getTextoNormalizado();
        c.setRfc(rfc.isEmpty() ? null : Validador.normalizarRfc(rfc));

        boolean exito;
        if (cliente == null) {
            exito = controlador.agregarCliente(c);
        } else {
            exito = controlador.actualizarCliente(c);
        }

        if (exito) {
            guardado = true;
            JOptionPane.showMessageDialog(this,
                    "Cliente guardado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el cliente. Verifique que el email no esté duplicado.",
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}
