package vista;
import controlador.ClienteControlador;
import modelo.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
// --- NUEVO: Imports para SwingWorker ---
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Panel para el CRUD (Gestión) de Clientes.
 * (Actualizado con SwingWorker)
 */
public class ClientesPanel extends JPanel {
    private final ClienteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Cliente> listaClientes; // Cache

    // --- NUEVO: Botones como variables de clase ---
    private final JButton btnAgregar;
    private final JButton btnEditar;

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

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar Cliente");
        btnEditar = new JButton("Editar Seleccionado");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());

        // Carga inicial de datos
        cargarClientes();
    }

    /**
     * --- MÉTODO MODIFICADO CON SWINGWORKER ---
     */
    private void cargarClientes() {
        setBotonesEnabled(false); // Deshabilitar botones
        modeloTabla.setRowCount(0); // Limpiar tabla

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Cliente> doInBackground() throws Exception {
                // Se ejecuta en otro hilo
                return controlador.listarClientes();
            }

            @Override
            protected void done() {
                try {
                    // Se ejecuta en el hilo de Swing
                    listaClientes = get(); // Obtener resultado

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
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(ClientesPanel.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true); // Volver a habilitar
                }
            }
        };
        worker.execute();
    }

    // --- NUEVO: Helper para habilitar/deshabilitar botones ---
    private void setBotonesEnabled(boolean enabled) {
        btnAgregar.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
    }

    // --- Métodos sin cambios ---
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
        Cliente c = listaClientes.get(fila);
        abrirFormulario(c);
    }
}