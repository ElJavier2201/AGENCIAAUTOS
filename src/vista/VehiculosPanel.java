package vista;

import controlador.VehiculoControlador;
import modelo.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para el CRUD (Gestión) de Vehículos (Inventario).
 * (Actualizado para modo Gerente y Vendedor)
 */
public class VehiculosPanel extends JPanel {
    private final VehiculoControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;

    // --- CAMBIO ---
    // El constructor ahora acepta un booleano
    public VehiculosPanel(boolean isGerente) {
        controlador = new VehiculoControlador();
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Inventario de Vehículos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Marca", "Modelo", "Año", "VIN", "Color", "Km", "Precio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);

        // Ocultar la columna ID (columna 0)
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        cargarVehiculos();
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- CAMBIO ---
        // Solo muestra los botones de administración si es Gerente
        if (isGerente) {
            JPanel panelBotones = new JPanel();
            JButton btnAgregar = new JButton("Agregar Vehículo");
            JButton btnEditar = new JButton("Editar Seleccionado");
            JButton btnVendido = new JButton("Marcar como Vendido"); // (Esto lo moveremos al panel de ventas)

            panelBotones.add(btnAgregar);
            panelBotones.add(btnEditar);
            panelBotones.add(btnVendido);

            add(panelBotones, BorderLayout.SOUTH);

            // Acciones de Gerente
            btnAgregar.addActionListener(e -> abrirFormulario(null));
            btnEditar.addActionListener(e -> abrirFormularioEditar());
            btnVendido.addActionListener(e -> marcarVendido());
        } else {
            // Si no es Gerente, solo muestra un botón de refrescar
            JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnRefrescar = new JButton("Refrescar Catálogo");
            btnRefrescar.addActionListener(e -> cargarVehiculos());
            panelSur.add(btnRefrescar);
            add(panelSur, BorderLayout.SOUTH);
        }
    }

    private void cargarVehiculos() {
        modeloTabla.setRowCount(0);
        // El controlador ya solo lista los disponibles
        List<Vehiculo> vehiculos = controlador.listarVehiculosDisponibles();
        for (Vehiculo v : vehiculos) {
            modeloTabla.addRow(new Object[]{
                    v.getIdVehiculo(),
                    v.getNombreMarca(),
                    v.getNombreModelo(),
                    v.getAnio(),
                    v.getNumeroSerie(),
                    v.getColor(),
                    v.getKilometraje(),
                    String.format("%.2f", v.getPrecio()),
                    v.getEstado()
            });
        }
    }

    // --- Métodos solo para Gerente ---

    private void abrirFormulario(Vehiculo vehiculo) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        VehiculoForma dialog = new VehiculoForma(owner, vehiculo);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarVehiculos();
        }
    }

    private void abrirFormularioEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para editar.");
            return;
        }

        int idVehiculo = (int) modeloTabla.getValueAt(fila, 0);

        // (Simplificación: esto debería usar un controlador.obtenerPorId(idVehiculo))
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo((int) modeloTabla.getValueAt(fila, 0));
        v.setNombreMarca((String) modeloTabla.getValueAt(fila, 1));
        v.setNombreModelo((String) modeloTabla.getValueAt(fila, 2));
        v.setAnio((int) modeloTabla.getValueAt(fila, 3));
        v.setNumeroSerie((String) modeloTabla.getValueAt(fila, 4));
        v.setColor((String) modeloTabla.getValueAt(fila, 5));
        v.setKilometraje((int) modeloTabla.getValueAt(fila, 6));
        v.setPrecio(Double.parseDouble(modeloTabla.getValueAt(fila, 7).toString()));
        v.setEstado((String) modeloTabla.getValueAt(fila, 8));

        abrirFormulario(v);
    }

    private void marcarVendido() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo.");
            return;
        }

        int idVehiculo = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1) + " " + modeloTabla.getValueAt(fila, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Marcar el vehículo '" + nombre + "' como VENDIDO?",
                "Confirmar Venta", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.marcarComoVendido(idVehiculo)) {
                JOptionPane.showMessageDialog(this, "Vehículo marcado como vendido.");
                cargarVehiculos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el estado.");
            }
        }
    }
}