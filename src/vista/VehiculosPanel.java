package vista;

import controlador.VehiculoControlador;
import modelo.Vehiculo;
import util.FiltrosPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Panel para el CRUD (Gestión) de Vehículos (Inventario).
 * ✅ MEJORADO: Con búsqueda avanzada y filtros
 */
public class VehiculosPanel extends JPanel {
    private final VehiculoControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vehiculo> listaVehiculos;
    private final JLabel lblPreviewImagen;
    private JButton btnAgregar, btnEditar, btnVendido, btnRefrescar;
    private FiltrosPanel filtrosPanel;

    // Filtros adicionales
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtPrecioMin, txtPrecioMax;
    private JSpinner spinnerAnioMin, spinnerAnioMax;

    public VehiculosPanel(boolean isGerente) {
        controlador = new VehiculoControlador();
        setLayout(new BorderLayout(0, 10));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("🚗 Inventario de Vehículos", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // ===== PANEL DE BÚSQUEDA Y FILTROS =====
        JPanel panelBusquedaCompleto = crearPanelBusqueda();

        // ===== TABLA =====
        String[] columnas = {"ID", "Marca", "Modelo", "Año", "VIN", "Color", "Km", "Precio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // ===== IMAGEN PREVIEW =====
        JPanel panelImagen = new JPanel(new BorderLayout());
        panelImagen.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                "Vista Previa",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));
        panelImagen.setBackground(Color.WHITE);

        lblPreviewImagen = new JLabel("Seleccione un vehículo", SwingConstants.CENTER);
        lblPreviewImagen.setPreferredSize(new Dimension(350, 350));
        lblPreviewImagen.setForeground(Color.GRAY);
        panelImagen.add(lblPreviewImagen, BorderLayout.CENTER);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelImagen);
        splitPane.setDividerLocation(650);
        splitPane.setBorder(null);

        // Panel que contiene búsqueda y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelBusquedaCompleto, BorderLayout.NORTH);
        panelCentral.add(splitPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // ===== LISTENER DE SELECCIÓN =====
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarImagenSeleccionada();
            }
        });

        // ===== BOTONES =====
        if (isGerente) {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            panelBotones.setBackground(new Color(236, 240, 241));

            btnAgregar = crearBoton("➕ Agregar Vehículo", new Color(46, 204, 113));
            btnEditar = crearBoton("✏️ Editar", new Color(52, 152, 219));
            btnVendido = crearBoton("✅ Marcar Vendido", new Color(230, 126, 34));

            panelBotones.add(btnAgregar);
            panelBotones.add(btnEditar);
            panelBotones.add(btnVendido);
            add(panelBotones, BorderLayout.SOUTH);

            btnAgregar.addActionListener(e -> abrirFormulario(null));
            btnEditar.addActionListener(e -> abrirFormularioEditar());
            btnVendido.addActionListener(e -> marcarVendido());
        } else {
            JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelSur.setBackground(new Color(236, 240, 241));
            btnRefrescar = crearBoton("🔄 Refrescar Catálogo", new Color(149, 165, 166));
            btnRefrescar.addActionListener(e -> cargarVehiculos());
            panelSur.add(btnRefrescar);
            add(panelSur, BorderLayout.SOUTH);
        }

        cargarVehiculos();
    }

    /**
     * ✅ NUEVO: Crea panel con búsqueda y filtros avanzados
     */
    private JPanel crearPanelBusqueda() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Búsqueda básica
        String[] filtros = {"Todos", "Por Marca", "Por Modelo", "Por VIN", "Por Color"};
        filtrosPanel = new FiltrosPanel("Buscar vehículo...", filtros);
        filtrosPanel.setBusquedaTiempoReal(true);
        filtrosPanel.setOnSearch(this::buscarVehiculo);
        filtrosPanel.setOnFilterChange(f -> buscarVehiculo(filtrosPanel.getTextoBusqueda()));

        panelPrincipal.add(filtrosPanel, BorderLayout.NORTH);

        // Filtros avanzados
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelFiltros.setBackground(new Color(236, 240, 241));
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Filtro por estado
        panelFiltros.add(new JLabel("Estado:"));
        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "nuevo", "usado", "reservado"});
        cbFiltroEstado.setPreferredSize(new Dimension(120, 25));
        cbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(cbFiltroEstado);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Filtro por precio
        panelFiltros.add(new JLabel("Precio:"));
        txtPrecioMin = new JTextField("0", 8);
        txtPrecioMax = new JTextField("999999", 8);
        panelFiltros.add(new JLabel("$"));
        panelFiltros.add(txtPrecioMin);
        panelFiltros.add(new JLabel("-"));
        panelFiltros.add(txtPrecioMax);

        JButton btnAplicarPrecio = new JButton("Aplicar");
        btnAplicarPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAplicarPrecio.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(btnAplicarPrecio);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Filtro por año
        panelFiltros.add(new JLabel("Año:"));
        spinnerAnioMin = new JSpinner(new SpinnerNumberModel(2000, 1990, 2030, 1));
        spinnerAnioMax = new JSpinner(new SpinnerNumberModel(2030, 1990, 2030, 1));
        spinnerAnioMin.setPreferredSize(new Dimension(70, 25));
        spinnerAnioMax.setPreferredSize(new Dimension(70, 25));
        spinnerAnioMin.addChangeListener(e -> aplicarFiltros());
        spinnerAnioMax.addChangeListener(e -> aplicarFiltros());
        panelFiltros.add(spinnerAnioMin);
        panelFiltros.add(new JLabel("-"));
        panelFiltros.add(spinnerAnioMax);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Botón limpiar filtros
        JButton btnLimpiarFiltros = new JButton("🗑️ Limpiar Filtros");
        btnLimpiarFiltros.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiarFiltros.setBackground(new Color(231, 76, 60));
        btnLimpiarFiltros.setForeground(Color.WHITE);
        btnLimpiarFiltros.setFocusPainted(false);
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        panelFiltros.add(btnLimpiarFiltros);

        panelPrincipal.add(panelFiltros, BorderLayout.CENTER);

        return panelPrincipal;
    }

    /**
     * ✅ NUEVO: Busca vehículos según texto y filtro
     */
    private void buscarVehiculo(String textoBusqueda) {
        aplicarFiltros();
    }

    /**
     * ✅ NUEVO: Aplica todos los filtros combinados
     */
    private void aplicarFiltros() {
        if (listaVehiculos == null || listaVehiculos.isEmpty()) {
            return;
        }

        String textoBusqueda = filtrosPanel.getTextoBusqueda().toLowerCase();
        String filtroTexto = filtrosPanel.getFiltroSeleccionado();
        String estadoSeleccionado = (String) cbFiltroEstado.getSelectedItem();

        double precioMin = 0;
        double precioMax = Double.MAX_VALUE;

        try {
            precioMin = Double.parseDouble(txtPrecioMin.getText());
            precioMax = Double.parseDouble(txtPrecioMax.getText());
        } catch (NumberFormatException e) {
            // Ignorar errores de formato
        }

        int anioMin = (int) spinnerAnioMin.getValue();
        int anioMax = (int) spinnerAnioMax.getValue();

        final double finalPrecioMin = precioMin;
        final double finalPrecioMax = precioMax;

        List<Vehiculo> resultado = listaVehiculos.stream()
                .filter(v -> {
                    // Filtro de texto
                    boolean coincideTexto = textoBusqueda.isEmpty() || coincideBusqueda(v, textoBusqueda, filtroTexto);

                    // Filtro de estado
                    assert estadoSeleccionado != null;
                    boolean coincideEstado = estadoSeleccionado.equals("Todos") ||
                            v.getEstado().equalsIgnoreCase(estadoSeleccionado);

                    // Filtro de precio
                    boolean coincidePrecio = v.getPrecio() >= finalPrecioMin && v.getPrecio() <= finalPrecioMax;

                    // Filtro de año
                    boolean coincideAnio = v.getAnio() >= anioMin && v.getAnio() <= anioMax;

                    return coincideTexto && coincideEstado && coincidePrecio && coincideAnio;
                })
                .collect(Collectors.toList());

        actualizarTabla(resultado);

        // Tooltip con resultados
        tabla.getTableHeader().setToolTipText(
                resultado.size() + " resultado(s) de " + listaVehiculos.size() + " total"
        );
    }

    /**
     * Verifica si el vehículo coincide con la búsqueda
     */
    private boolean coincideBusqueda(Vehiculo v, String busqueda, String filtro) {
        return switch (filtro) {
            case "Por Marca" -> v.getNombreMarca() != null && v.getNombreMarca().toLowerCase().contains(busqueda);
            case "Por Modelo" -> v.getNombreModelo() != null && v.getNombreModelo().toLowerCase().contains(busqueda);
            case "Por VIN" -> v.getNumeroSerie() != null && v.getNumeroSerie().toLowerCase().contains(busqueda);
            case "Por Color" -> v.getColor() != null && v.getColor().toLowerCase().contains(busqueda);
            default -> // "Todos"
                    (v.getNombreMarca() != null && v.getNombreMarca().toLowerCase().contains(busqueda)) ||
                            (v.getNombreModelo() != null && v.getNombreModelo().toLowerCase().contains(busqueda)) ||
                            (v.getNumeroSerie() != null && v.getNumeroSerie().toLowerCase().contains(busqueda)) ||
                            (v.getColor() != null && v.getColor().toLowerCase().contains(busqueda));
        };
    }

    /**
     * Limpia todos los filtros
     */
    private void limpiarFiltros() {
        filtrosPanel.limpiarBusqueda();
        cbFiltroEstado.setSelectedIndex(0);
        txtPrecioMin.setText("0");
        txtPrecioMax.setText("999999");
        spinnerAnioMin.setValue(2000);
        spinnerAnioMax.setValue(2030);
        actualizarTabla(listaVehiculos);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void cargarVehiculos() {
        setBotonesEnabled(false);
        modeloTabla.setRowCount(0);
        lblPreviewImagen.setIcon(null);
        lblPreviewImagen.setText("Cargando vehículos...");

        SwingWorker<List<Vehiculo>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vehiculo> doInBackground() {
                return controlador.listarVehiculosDisponibles();
            }
            @Override
            protected void done() {
                try {
                    listaVehiculos = get();
                    actualizarTabla(listaVehiculos);
                    lblPreviewImagen.setText("Seleccione un vehículo");
                } catch (InterruptedException | ExecutionException e) {
                    lblPreviewImagen.setText("Error al cargar");
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void actualizarTabla(List<Vehiculo> vehiculos) {
        modeloTabla.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            modeloTabla.addRow(new Object[]{
                    v.getIdVehiculo(),
                    v.getNombreMarca(),
                    v.getNombreModelo(),
                    v.getAnio(),
                    v.getNumeroSerie(),
                    v.getColor(),
                    v.getKilometraje(),
                    String.format("$%.2f", v.getPrecio()),
                    v.getEstado()
            });
        }
    }

    private void mostrarImagenSeleccionada() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("Seleccione un vehículo");
            return;
        }

        Vehiculo v = listaVehiculos.get(filaSeleccionada);
        String nombreArchivo = v.getImagenPath();

        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            nombreArchivo = "sin_imagen.png";
        }

        String rutaRecurso = "/recursos/imagenes_autos/" + nombreArchivo;
        URL imgUrl = getClass().getResource(rutaRecurso);

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage();
            int lblWidth = lblPreviewImagen.getWidth() > 0 ? lblPreviewImagen.getWidth() : 350;
            int lblHeight = lblPreviewImagen.getHeight() > 0 ? lblPreviewImagen.getHeight() : 350;
            Image newImg = img.getScaledInstance(lblWidth, lblHeight, Image.SCALE_SMOOTH);
            lblPreviewImagen.setIcon(new ImageIcon(newImg));
            lblPreviewImagen.setText(null);
        } else {
            lblPreviewImagen.setIcon(null);
            lblPreviewImagen.setText("Imagen no encontrada");
        }
    }

    private void setBotonesEnabled(boolean enabled) {
        if (btnAgregar != null) btnAgregar.setEnabled(enabled);
        if (btnEditar != null) btnEditar.setEnabled(enabled);
        if (btnVendido != null) btnVendido.setEnabled(enabled);
        if (btnRefrescar != null) btnRefrescar.setEnabled(enabled);
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