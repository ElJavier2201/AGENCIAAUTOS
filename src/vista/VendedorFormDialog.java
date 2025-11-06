package vista;
import controlador.VendedorControlador;
import modelo.Vendedor;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

/**
 * JDialog para agregar o editar un Vendedor.
 */
public class VendedorFormDialog extends JDialog {

    private final VendedorControlador controlador;
    private final Vendedor vendedor; // null si es 'nuevo'
    private boolean guardado = false;

    // Componentes del Formulario
    private JTextField txtNombre, txtApellido, txtTelefono, txtEmail, txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtFechaContratacion; // Simplificado como YYYY-MM-DD
    private JSpinner spinnerComision;
    private JComboBox<String> cbRol;
    private JCheckBox chkActivo;

    public VendedorFormDialog(Frame owner, Vendedor vendedor) {
        super(owner, true);
        this.vendedor = vendedor;
        this.controlador = new VendedorControlador();

        setTitle(vendedor == null ? "Agregar Nuevo Vendedor" : "Editar Vendedor");
        setSize(450, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        if (vendedor != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtTelefono = new JTextField();
        txtEmail = new JTextField();
        txtFechaContratacion = new JTextField();
        txtFechaContratacion.setToolTipText("Formato: YYYY-MM-DD");
        spinnerComision = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 100.0, 0.5));
        txtUsuario = new JTextField();
        txtContrasena = new JPasswordField();
        cbRol = new JComboBox<>(new String[]{"Vendedor", "Gerente"});
        chkActivo = new JCheckBox("Activo", true);

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Fecha Contratación (YYYY-MM-DD):"));
        panel.add(txtFechaContratacion);
        panel.add(new JLabel("Comisión (%):"));
        panel.add(spinnerComision);
        panel.add(new JLabel("Usuario (Login):"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel("Rol:"));
        panel.add(cbRol);
        panel.add(new JLabel("Estado:"));
        panel.add(chkActivo);

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
        txtContrasena.setToolTipText("Dejar en blanco para no cambiar");
        cbRol.setSelectedItem(vendedor.getRol());
        chkActivo.setSelected(vendedor.isActivo());
    }

    private void guardar() {
        if (txtUsuario.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Usuario son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación de contraseña para usuario nuevo
        String password = new String(txtContrasena.getPassword());
        if (vendedor == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevos vendedores.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Vendedor v = (vendedor == null) ? new Vendedor() : this.vendedor;
            v.setNombre(txtNombre.getText());
            v.setApellido(txtApellido.getText());
            v.setTelefono(txtTelefono.getText());
            v.setEmail(txtEmail.getText());
            v.setFechaContratacion(Date.valueOf(txtFechaContratacion.getText())); // YYYY-MM-DD
            v.setComisionPorcentaje((Double) spinnerComision.getValue());
            v.setActivo(chkActivo.isSelected());
            v.setUsuario(txtUsuario.getText());
            v.setContraseña(password); // El DAO sabrá si hashearla o no
            v.setRol((String) cbRol.getSelectedItem());

            boolean exito;
            if (vendedor == null) {
                exito = controlador.agregarVendedor(v);
            } else {
                exito = controlador.actualizarVendedor(v);
            }

            if (exito) {
                guardado = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el vendedor (verifique que el email o usuario no estén repetidos).", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error en el formato de la fecha. Use YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}