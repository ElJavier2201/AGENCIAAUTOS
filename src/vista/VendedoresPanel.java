package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
// --- NUEVO: Imports para SwingWorker ---
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Panel para el CRUD (Gestión) de Vendedores.
 * (Actualizado con SwingWorker)
 */
public class VendedoresPanel extends JPanel {
    private final VendedorControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vendedor> listaVendedores; // Cache de la lista

    // --- NUEVO: Botones como variables de clase ---
    private final JButton btnAgregar;
    private final JButton btnEditar;
    private final JButton btnActivar;

    public VendedoresPanel() {
        controlador = new VendedorControlador();
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Vendedores", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol", "Email", "Teléfono", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);
        tabla.getColumnModel().getColumn(6).setMaxWidth(60);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar Vendedor");
        btnEditar = new JButton("Editar Seleccionado");
        btnActivar = new JButton("Activar/Desactivar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnActivar);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnActivar.addActionListener(e -> toggleActivo());

        // Carga inicial de datos
        cargarVendedores();
    }

    /**
     * --- MÉTODO MODIFICADO CON SWINGWORKER ---
     */
    private void cargarVendedores() {
        setBotonesEnabled(false); // Deshabilitar
        modeloTabla.setRowCount(0);

        SwingWorker<List<Vendedor>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Vendedor> doInBackground() throws Exception {
                // Se ejecuta en otro hilo
                return controlador.listarVendedores();
            }

            @Override
            protected void done() {
                try {
                    // Se ejecuta en el hilo de Swing
                    listaVendedores = get(); // Obtener resultado

                    for (Vendedor v : listaVendedores) {
                        modeloTabla.addRow(new Object[]{
                                v.getIdVendedor(),
                                v.getNombre() + " " + v.getApellido(),
                                v.getUsuario(),
                                v.getRol(),
                                v.getEmail(),
                                v.getTelefono(),
                                v.isActivo() ? "Sí" : "No"
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(VendedoresPanel.this,
                            "Error al cargar vendedores: " + e.getMessage(),
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
        btnActivar.setEnabled(enabled);
    }

    // --- Métodos sin cambios ---
    private void abrirFormulario(Vendedor vendedor) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        VendedorFormDialog dialog = new VendedorFormDialog(owner, vendedor);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarVendedores();
        }
    }

    private void abrirFormularioEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vendedor para editar.");
            return;
        }
        Vendedor v = listaVendedores.get(fila);
        abrirFormulario(v);
    }

    private void toggleActivo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vendedor para activar o desactivar.");
            return;
        }

        Vendedor v = listaVendedores.get(fila);
        v.setActivo(!v.isActivo());

        if (controlador.actualizarVendedor(v)) {
            JOptionPane.showMessageDialog(this, "Estado del vendedor actualizado.");
            cargarVendedores();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado.");
        }
    }
}