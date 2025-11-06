package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para el CRUD (Gestión) de Vendedores.
 */
public class VendedoresPanel extends JPanel {
    private final VendedorControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vendedor> listaVendedores; // Cache de la lista

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
        tabla.getColumnModel().getColumn(0).setMaxWidth(40); // ID pequeño
        tabla.getColumnModel().getColumn(6).setMaxWidth(60); // Activo

        cargarVendedores();
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar Vendedor");
        JButton btnEditar = new JButton("Editar Seleccionado");
        JButton btnActivar = new JButton("Activar/Desactivar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnActivar);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnActivar.addActionListener(e -> toggleActivo());
    }

    private void cargarVendedores() {
        modeloTabla.setRowCount(0);
        listaVendedores = controlador.listarVendedores(); // Cargar la lista
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
    }

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
        // Obtener el objeto Vendedor completo de la lista cacheada
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
        v.setActivo(!v.isActivo()); // Invertir el estado

        if (controlador.actualizarVendedor(v)) {
            JOptionPane.showMessageDialog(this, "Estado del vendedor actualizado.");
            cargarVendedores();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado.");
        }
    }
}
