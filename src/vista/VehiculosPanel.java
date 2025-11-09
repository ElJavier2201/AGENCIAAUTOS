package vista;

import controlador.VehiculoControlador;
import modelo.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
// --- NUEVO: Import para URL (Recursos) ---
import java.net.URL;

/**
 * Panel para el CRUD (Gestión) de Vehículos (Inventario).
 * (Actualizado para cargar imágenes desde recursos del classpath)
 */
public class VehiculosPanel extends JPanel {
    private final VehiculoControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vehiculo> listaVehiculos;
    private JLabel lblPreviewImagen;
    private JButton btnAgregar, btnEditar, btnVendido, btnRefrescar;

    public VehiculosPanel(boolean isGerente) {
        // ... (Constructor sin cambios, igual al anterior)
        controlador = new VehiculoControlador();
        setLayout(new BorderLayout());
        JLabel titulo = new JLabel("Inventario de Vehículos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);
        String[] columnas = {"ID", "Marca", "Modelo", "Año", "VIN", "Color", "Km", "Precio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) { /* ... */ };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        JPanel panelImagen = new JPanel(new BorderLayout());
        panelImagen.setBorder(BorderFactory.createTitledBorder("Vista Previa"));
        lblPreviewImagen = new JLabel("(Seleccione un vehículo de la tabla)", SwingConstants.CENTER);
        lblPreviewImagen.setPreferredSize(new Dimension(350, 350));
        panelImagen.add(lblPreviewImagen, BorderLayout.CENTER);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelImagen);
        splitPane.setDividerLocation(650);
        add(splitPane, BorderLayout.CENTER);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarImagenSeleccionada();
            }
        });
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
        cargarVehiculos();
    }

    private void cargarVehiculos() {
        // ... (Este método no cambia, sigue usando SwingWorker)
        setBotonesEnabled(false);
        modeloTabla.setRowCount(0);
        lblPreviewImagen.setIcon(null);
        lblPreviewImagen.setText("Cargando vehículos...");

        SwingWorker<List<Vehiculo>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vehiculo> doInBackground() throws Exception {
                return controlador.listarVehiculosDisponibles();
            }
            @Override
            protected void done() {
                try {
                    listaVehiculos = get();
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
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void setBotonesEnabled(boolean enabled) {
        // ... (Este método no cambia)
        if (btnAgregar != null) btnAgregar.setEnabled(enabled);
        if (btnEditar != null) btnEditar.setEnabled(enabled);
        if (btnVendido != null) btnVendido.setEnabled(enabled);
        if (btnRefrescar != null) btnRefrescar.setEnabled(enabled);
    }

    /**
     * --- MÉTODO MODIFICADO ---
     * Carga la imagen desde el paquete de RECURSOS, no desde el disco C:\.
     */
    private void mostrarImagenSeleccionada() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("(Seleccione un vehículo de la tabla)");
            return;
        }

        Vehiculo v = listaVehiculos.get(filaSeleccionada);
        String nombreArchivo = v.getImagenPath(); // Ej: "mustang.jpg"

        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {

            // --- ESTA ES LA LÓGICA DE CARGA DE RECURSOS ---
            // Construimos la ruta *dentro* del classpath
            String rutaRecurso = "/recursos/imagenes_autos/" + nombreArchivo;

            // Pedimos al ClassLoader que encuentre el recurso
            URL imgUrl = getClass().getResource(rutaRecurso);

            if (imgUrl != null) {
                // Si se encontró el recurso
                ImageIcon icon = new ImageIcon(imgUrl);

                Image img = icon.getImage();
                int lblWidth = lblPreviewImagen.getWidth();
                int lblHeight = lblPreviewImagen.getHeight();

                if (lblWidth <= 0) lblWidth = 350;
                if (lblHeight <= 0) lblHeight = 350;

                Image newImg = img.getScaledInstance(lblWidth, lblHeight, Image.SCALE_SMOOTH);

                lblPreviewImagen.setIcon(new ImageIcon(newImg));
                lblPreviewImagen.setText(null);
            } else {
                // El archivo (ej. "mustang.jpg") no se encontró en la carpeta de recursos
                lblPreviewImagen.setIcon(null);
                lblPreviewImagen.setText("Imagen no encontrada");
            }

        } else {
            // El campo imagen_path está NULL o vacío en la BD
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("Imagen no disponible");
        }
    }

    // --- (El resto de métodos: abrirFormulario, abrirFormularioEditar, marcarVendido... no cambian) ---
    private void abrirFormulario(Vehiculo vehiculo) { /* ... */ }
    private void abrirFormularioEditar() { /* ... */ }
    private void marcarVendido() { /* ... */ }
}