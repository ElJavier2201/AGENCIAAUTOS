package vista;

import controlador.ClienteControlador;
import modelo.Cliente;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * NUEVO: Dialog para que los clientes se identifiquen
 * Simplificado: selección por nombre (en producción usarías email+contraseña)
 */
public class ClienteLoginDialog extends JDialog {

    private final ClienteControlador controlador;
    private Cliente clienteAutenticado = null;
    private final JComboBox<Cliente> cbClientes;

    public ClienteLoginDialog(Frame owner) {
        super(owner, "Identificación de Cliente", true);
        this.controlador = new ClienteControlador();

        setSize(400, 200);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel de instrucciones
        JPanel panelNorte = new JPanel();
        JLabel lblTitulo = new JLabel("Seleccione su nombre para continuar:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelNorte.add(lblTitulo);
        add(panelNorte, BorderLayout.NORTH);

        // Panel central con combo
        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        cbClientes = new JComboBox<>();
        cbClientes.setPreferredSize(new Dimension(300, 30));

        // Configurar renderizado del ComboBox
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente c) {
                    setText(c.getNombre() + " " + c.getApellido() + " - " + c.getEmail());
                }
                return this;
            }
        });

        panelCentro.add(new JLabel("Cliente:"));
        panelCentro.add(cbClientes);
        add(panelCentro, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnCancelar = new JButton("Cancelar");

        btnIngresar.addActionListener(e -> ingresar());
        btnCancelar.addActionListener(e -> dispose());

        panelSur.add(btnIngresar);
        panelSur.add(btnCancelar);
        add(panelSur, BorderLayout.SOUTH);

        // Cargar clientes
        cargarClientes();
    }

    private void cargarClientes() {
        cbClientes.removeAllItems();

        // Cargar en un hilo de fondo
        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() {
                return controlador.listarClientes();
            }

            @Override
            protected void done() {
                try {
                    List<Cliente> clientes = get();
                    for (Cliente c : clientes) {
                        cbClientes.addItem(c);
                    }

                    if (clientes.isEmpty()) {
                        JOptionPane.showMessageDialog(ClienteLoginDialog.this,
                                "No hay clientes registrados en el sistema.",
                                "Sin Clientes",
                                JOptionPane.WARNING_MESSAGE);
                        dispose();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClienteLoginDialog.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        };
        worker.execute();
    }

    private void ingresar() {
        Cliente seleccionado = (Cliente) cbClientes.getSelectedItem();

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un cliente.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        clienteAutenticado = seleccionado;
        dispose();
    }

    public Cliente getClienteAutenticado() {
        return clienteAutenticado;
    }
}