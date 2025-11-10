package vista;

import controlador.VehiculoControlador;
import modelo.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Panel para el CRUD (Gestión) de Vehículos (Inventario).
 * (Actualizado con SwingWorker para carga en segundo plano)
 */
public class VehiculosPanel extends JPanel {
    private final VehiculoControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vehiculo> listaVehiculos;
    private final JLabel lblPreviewImagen;

    // --- NUEVO: Variables para los botones (para habilitar/deshabilitar) ---
    private JButton btnAgregar, btnEditar, btnVendido, btnRefrescar;

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
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        // Panel de la imagen
        JPanel panelImagen = new JPanel(new BorderLayout());
        panelImagen.setBorder(BorderFactory.createTitledBorder("Vista Previa"));
        lblPreviewImagen = new JLabel("(Seleccione un vehículo de la tabla)", SwingConstants.CENTER);
        lblPreviewImagen.setPreferredSize(new Dimension(350, 350));
        panelImagen.add(lblPreviewImagen, BorderLayout.CENTER);

        // JSplitPane
        JScrollPane scrollTabla = new JScrollPane(tabla);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelImagen);
        splitPane.setDividerLocation(650);
        add(splitPane, BorderLayout.CENTER);

        // Listener para la selección
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarImagenSeleccionada();
            }
        });

        // --- MODIFICADO: Panel de Botones ---
        // (Los creamos aquí para poder acceder a ellos desde setBotonesEnabled)
        if (isGerente) {
            JPanel panelBotones = new JPanel();
            btnAgregar = new JButton("Agregar Vehículo");
            btnEditar = new JButton("Editar Seleccionado");
            btnVendido = new JButton("Marcar como Vendido");

            panelBotones.add(btnAgregar);
            panelBotones.add(btnEditar);
            panelBotones.add(btnVendido);
            add(panelBotones, BorderLayout.SOUTH);

            btnAgregar.addActionListener(e -> abrirFormulario(null));
            btnEditar.addActionListener(e -> abrirFormularioEditar());
            btnVendido.addActionListener(e -> marcarVendido());
        } else {
            JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnRefrescar = new JButton("Refrescar Catálogo");
            btnRefrescar.addActionListener(e -> cargarVehiculos());
            panelSur.add(btnRefrescar);
            add(panelSur, BorderLayout.SOUTH);
        }

        // Carga inicial de datos
        cargarVehiculos();
    }

    /**
     * Carga la lista de vehículos en un hilo de fondo.
     */
    private void cargarVehiculos() {
        setBotonesEnabled(false); // Deshabilitar botones
        modeloTabla.setRowCount(0); // Limpiar tabla
        lblPreviewImagen.setIcon(null);
        lblPreviewImagen.setText("Cargando vehículos..."); // Mensaje de carga

        SwingWorker<List<Vehiculo>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Vehiculo> doInBackground() throws Exception {
                // Esta es la llamada pesada, se ejecuta en otro hilo
                return controlador.listarVehiculosDisponibles();
            }

            @Override
            protected void done() {
                try {
                    // Esto se ejecuta en el hilo de Swing cuando doInBackground termina
                    listaVehiculos = get(); // Obtener el resultado

                    for (Vehiculo v : listaVehiculos) {
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
                    lblPreviewImagen.setText("(Seleccione un vehículo de la tabla)");
                } catch (InterruptedException | ExecutionException e) {
                    lblPreviewImagen.setText("Error al cargar vehículos");
                    JOptionPane.showMessageDialog(VehiculosPanel.this,
                            "Error al cargar vehículos: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true); // Volver a habilitar botones
                }
            }
        };

        worker.execute(); // Iniciar el worker
    }

    // --- NUEVO: Helper para habilitar/deshabilitar botones ---
    private void setBotonesEnabled(boolean enabled) {
        if (btnAgregar != null) btnAgregar.setEnabled(enabled);
        if (btnEditar != null) btnEditar.setEnabled(enabled);
        if (btnVendido != null) btnVendido.setEnabled(enabled);
        if (btnRefrescar != null) btnRefrescar.setEnabled(enabled);
    }

    // --- Método sin cambios ---
    private void mostrarImagenSeleccionada() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("(Seleccione un vehículo de la tabla)");
            return;
        }

        Vehiculo v = listaVehiculos.get(filaSeleccionada);
        String path = v.getImagenPath();

        if (path != null && !path.isEmpty()) {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            int lblWidth = lblPreviewImagen.getWidth();
            int lblHeight = lblPreviewImagen.getHeight();

            if (lblWidth <= 0) lblWidth = 350;
            if (lblHeight <= 0) lblHeight = 350;

            Image newImg = img.getScaledInstance(lblWidth, lblHeight, Image.SCALE_SMOOTH);
            lblPreviewImagen.setIcon(new ImageIcon(newImg));
            lblPreviewImagen.setText(null);
        } else {
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("Imagen no disponible");
        }
    }

    // --- Métodos de Gerente sin cambios ---
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
        Vehiculo v = listaVehiculos.get(fila);
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