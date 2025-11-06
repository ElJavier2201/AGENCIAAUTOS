package vista;
import controlador.ClienteControlador;
import modelo.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para el CRUD (Gestión) de Clientes.
 */
public class ClientesPanel extends JPanel {
    private final ClienteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Cliente> listaClientes; // Cache

    public ClientesPanel() {
        controlador = new ClienteControlador();
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "RFC", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(40); // ID pequeño

        cargarClientes();
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar Cliente");
        JButton btnEditar = new JButton("Editar Seleccionado");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        listaClientes = controlador.listarClientes(); // Cargar la lista
        for (Cliente c : listaClientes) {
            modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre() + " " + c.getApellido(),
                    c.getEmail(),
                    c.getTelefono(),
                    c.getRfc(),
                    c.getDireccion()
            });
        }
    }

    private void abrirFormulario(Cliente cliente) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ClienteFormDialog dialog = new ClienteFormDialog(owner, cliente);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarClientes();
        }
    }

    private void abrirFormularioEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar.");
            return;
        }
        // Obtener el objeto Cliente completo de la lista cacheada
        Cliente c = listaClientes.get(fila);
        abrirFormulario(c);
    }
}
