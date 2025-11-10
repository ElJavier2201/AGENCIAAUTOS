package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;
import util.ValidadorSwing;
import util.Validador;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

/**
 * JDialog para agregar o editar un Vendedor.
 * ✅ ACTUALIZADO: Con validaciones en tiempo real
 */
public class VendedorFormDialog extends JDialog {

    private final VendedorControlador controlador;
    private final Vendedor vendedor;
    private boolean guardado = false;

    // Componentes con validación
    private ValidadorSwing txtNombre, txtApellido, txtTelefono, txtEmail, txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtFechaContratacion;
    private JSpinner spinnerComision;
    private JComboBox<String> cbRol;
    private JCheckBox chkActivo;

    // Labels de error
    private JLabel lblErrorNombre, lblErrorApellido, lblErrorTelefono;
    private JLabel lblErrorEmail, lblErrorUsuario, lblErrorPassword;

    public VendedorFormDialog(Frame owner, Vendedor vendedor) {
        super(owner, true);
        this.vendedor = vendedor;
        this.controlador = new VendedorControlador();

        setTitle(vendedor == null ? "Agregar Nuevo Vendedor" : "Editar Vendedor");
        setSize(550, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        if (vendedor != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.insets = new Insets(5, 5, 2, 5);
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
        panel.add(txtEmail, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorEmail = new JLabel();
        lblErrorEmail.setFont(new Font("Arial", Font.ITALIC, 10));
        txtEmail.setLabelError(lblErrorEmail);
        panel.add(lblErrorEmail, gbc);

        // ===== FECHA CONTRATACIÓN =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Fecha Contratación (YYYY-MM-DD): *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFechaContratacion = new JTextField();
        txtFechaContratacion.setToolTipText("Formato: YYYY-MM-DD. Ej: 2024-01-15");
        panel.add(txtFechaContratacion, gbc);

        // ===== COMISIÓN =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Comisión (%): *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        spinnerComision = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 100.0, 0.5));
        panel.add(spinnerComision, gbc);

        // ===== USUARIO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Usuario (Login): *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtUsuario = new ValidadorSwing(20);
        txtUsuario.setValidador(Validador::validarUsuario);
        txtUsuario.setToolTipText("4-20 caracteres: letras, números, guión bajo");
        panel.add(txtUsuario, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorUsuario = new JLabel();
        lblErrorUsuario.setFont(new Font("Arial", Font.ITALIC, 10));
        txtUsuario.setLabelError(lblErrorUsuario);
        panel.add(lblErrorUsuario, gbc);

        // ===== CONTRASEÑA =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        String labelPass = (vendedor == null) ? "Contraseña: *" : "Contraseña: (dejar vacío para no cambiar)";
        panel.add(new JLabel(labelPass), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtContrasena = new JPasswordField(20);
        txtContrasena.setToolTipText("Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número");
        panel.add(txtContrasena, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblErrorPassword = new JLabel();
        lblErrorPassword.setFont(new Font("Arial", Font.ITALIC, 10));
        lblErrorPassword.setForeground(Color.RED);
        panel.add(lblErrorPassword, gbc);

        // ===== ROL =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Rol: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        cbRol = new JComboBox<>(new String[]{"Vendedor", "Gerente"});
        panel.add(cbRol, gbc);

        // ===== ACTIVO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Estado:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        chkActivo = new JCheckBox("Activo", true);
        panel.add(chkActivo, gbc);

        // Nota
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
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
        txtNombre.setText(vendedor.getNombre());
        txtApellido.setText(vendedor.getApellido());
        txtTelefono.setText(vendedor.getTelefono());
        txtEmail.setText(vendedor.getEmail());
        txtFechaContratacion.setText(vendedor.getFechaContratacion().toString());
        spinnerComision.setValue(vendedor.getComisionPorcentaje());
        txtUsuario.setText(vendedor.getUsuario());
        cbRol.setSelectedItem(vendedor.getRol());
        chkActivo.setSelected(vendedor.isActivo());
    }

    private void guardar() {
        // ✅ VALIDAR CAMPOS
        boolean valido = true;

        valido &= txtNombre.validar();
        valido &= txtApellido.validar();
        valido &= txtTelefono.validar();
        valido &= txtEmail.validar();
        valido &= txtUsuario.validar();

        // Validar contraseña (solo si es nuevo o si se ingresó algo)
        String password = new String(txtContrasena.getPassword());
        if (vendedor == null) {
            // Nuevo vendedor: contraseña obligatoria
            String errorPass = Validador.validarPassword(password);
            if (errorPass != null) {
                lblErrorPassword.setText(errorPass);
                valido = false;
            } else {
                lblErrorPassword.setText("");
            }
        } else if (!password.isEmpty()) {
            // Edición con cambio de contraseña
            String errorPass = Validador.validarPassword(password);
            if (errorPass != null) {
                lblErrorPassword.setText(errorPass);
                valido = false;
            } else {
                lblErrorPassword.setText("");
            }
        }

        if (!valido) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los errores marcados antes de guardar.",
                    "Errores de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vendedor v = (vendedor == null) ? new Vendedor() : this.vendedor;

            v.setNombre(Validador.normalizarNombre(txtNombre.getTextoNormalizado()));
            v.setApellido(Validador.normalizarNombre(txtApellido.getTextoNormalizado()));
            v.setTelefono(Validador.normalizarTelefono(txtTelefono.getTextoNormalizado()));
            v.setEmail(txtEmail.getTextoNormalizado().toLowerCase());
            v.setFechaContratacion(Date.valueOf(txtFechaContratacion.getText().trim()));
            v.setComisionPorcentaje((Double) spinnerComision.getValue());
            v.setActivo(chkActivo.isSelected());
            v.setUsuario(txtUsuario.getTextoNormalizado());
            v.setContraseña(password);
            v.setRol((String) cbRol.getSelectedItem());

            boolean exito;
            if (vendedor == null) {
                exito = controlador.agregarVendedor(v);
            } else {
                exito = controlador.actualizarVendedor(v);
            }

            if (exito) {
                guardado = true;
                JOptionPane.showMessageDialog(this,
                        "Vendedor guardado exitosamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar. Verifique que el email o usuario no estén duplicados.",
                        "Error de Base de Datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en el formato de la fecha. Use YYYY-MM-DD",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}