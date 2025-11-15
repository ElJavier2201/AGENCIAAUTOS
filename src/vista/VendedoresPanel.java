package vista;

import controlador.VendedorControlador;
import modelo.Vendedor;
import util.FiltrosPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Panel para el CRUD (Gestión) de Vendedores.
 */
public class VendedoresPanel extends JPanel {
    private final VendedorControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Vendedor> listaVendedores;
    private final JButton btnAgregar;
    private final JButton btnEditar;
    private final JButton btnActivar;
    private FiltrosPanel filtrosPanel;
    private JComboBox<String> cbFiltroRol;
    private JComboBox<String> cbFiltroEstado;

    public VendedoresPanel() {
        controlador = new VendedorControlador();
        setLayout(new BorderLayout(0, 10));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel(" Gestión de Vendedores", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);
        JPanel panelBusquedaCompleto = crearPanelBusqueda();

        String[] columnas = {"ID", "Nombre Completo", "Usuario", "Rol", "Email", "Teléfono", "Comisión %", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true); // Rellena el espacio vacío si hay pocas filas
        tabla.setRowHeight(28); // Un poco más de altura de fila
        tabla.setGridColor(new Color(220, 220, 220)); // Líneas de grid sutiles
        tabla.setIntercellSpacing(new Dimension(0, 0));

        // Estilo del Header (Cabecera)
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 35));
        header.setBackground(new Color(52, 73, 94)); // Color de la barra lateral
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false); // Opcional: deshabilitar reordenar columnas
        tabla.setDefaultRenderer(Object.class, new util.EstiloTabla());
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.getColumnModel().getColumn(3).setMaxWidth(80); // Rol
        tabla.getColumnModel().getColumn(6).setMaxWidth(90); // Comisión
        tabla.getColumnModel().getColumn(7).setMaxWidth(60); // Activo
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Panel central que contiene búsqueda y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelBusquedaCompleto, BorderLayout.NORTH);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        btnAgregar = crearBoton(" Agregar Vendedor", new Color(46, 204, 113));
        btnEditar = crearBoton(" Editar Seleccionado", new Color(52, 152, 219));
        btnActivar = crearBoton(" Activar/Desactivar", new Color(230, 126, 34));
        JButton btnRefrescar = crearBoton(" Refrescar", new Color(149, 165, 166));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnActivar);
        panelBotones.add(btnRefrescar);

        add(panelBotones, BorderLayout.SOUTH);

        // ===== ACCIONES =====
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnActivar.addActionListener(e -> toggleActivo());
        btnRefrescar.addActionListener(e -> {
            filtrosPanel.limpiarBusqueda();
            limpiarFiltros();
            cargarVendedores();
        });

        // Doble click para editar
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirFormularioEditar();
                }
            }
        });

        // Carga inicial de datos
        cargarVendedores();
    }

    /**
     *  Crea panel con búsqueda y filtros
     */
    private JPanel crearPanelBusqueda() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Búsqueda básica
        String[] filtros = {"Todos", "Por Nombre", "Por Usuario", "Por Email"};
        filtrosPanel = new FiltrosPanel("Buscar vendedor...", filtros);
        filtrosPanel.setBusquedaTiempoReal(true);
        filtrosPanel.setOnSearch(this::aplicarFiltros);
        filtrosPanel.setOnFilterChange(f -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));

        panelPrincipal.add(filtrosPanel, BorderLayout.NORTH);

        // Filtros adicionales
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelFiltros.setBackground(new Color(236, 240, 241));
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Filtro por rol
        panelFiltros.add(new JLabel("Rol:"));
        cbFiltroRol = new JComboBox<>(new String[]{"Todos", "Gerente", "Vendedor"});
        cbFiltroRol.setPreferredSize(new Dimension(120, 25));
        cbFiltroRol.addActionListener(e -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));
        panelFiltros.add(cbFiltroRol);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Filtro por estado
        panelFiltros.add(new JLabel("Estado:"));
        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Activos", "Inactivos"});
        cbFiltroEstado.setPreferredSize(new Dimension(120, 25));
        cbFiltroEstado.addActionListener(e -> aplicarFiltros(filtrosPanel.getTextoBusqueda()));
        panelFiltros.add(cbFiltroEstado);

        panelFiltros.add(new JSeparator(SwingConstants.VERTICAL));

        // Estadísticas rápidas
        JLabel lblStats = new JLabel("Total: 0 | Activos: 0 | Inactivos: 0");
        lblStats.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStats.setForeground(new Color(127, 140, 141));
        panelFiltros.add(lblStats);

        panelPrincipal.add(panelFiltros, BorderLayout.CENTER);

        return panelPrincipal;
    }

    /**
     *Aplica todos los filtros combinados
     */
    private void aplicarFiltros(String textoBusqueda) {
        if (listaVendedores == null || listaVendedores.isEmpty()) {
            return;
        }

        String busqueda = textoBusqueda.toLowerCase();
        String filtroTexto = filtrosPanel.getFiltroSeleccionado();
        String rolSeleccionado = (String) cbFiltroRol.getSelectedItem();
        String estadoSeleccionado = (String) cbFiltroEstado.getSelectedItem();

        List<Vendedor> resultado = listaVendedores.stream()
                .filter(v -> {
                    // Filtro de texto
                    boolean coincideTexto = busqueda.isEmpty() || coincideBusqueda(v, busqueda, filtroTexto);

                    // Filtro de rol
                    assert rolSeleccionado != null;
                    boolean coincideRol = rolSeleccionado.equals("Todos") ||
                            v.getRol().equalsIgnoreCase(rolSeleccionado);

                    // Filtro de estado
                    boolean coincideEstado;
                    assert estadoSeleccionado != null;
                    if (estadoSeleccionado.equals("Activos")) {
                        coincideEstado = v.isActivo();
                    } else if (estadoSeleccionado.equals("Inactivos")) {
                        coincideEstado = !v.isActivo();
                    } else {
                        coincideEstado = true; // "Todos"
                    }

                    return coincideTexto && coincideRol && coincideEstado;
                })
                .collect(Collectors.toList());

        actualizarTabla(resultado);

        // Actualizar estadísticas
        long activos = resultado.stream().filter(Vendedor::isActivo).count();
        long inactivos = resultado.size() - activos;

        // Buscar el label de estadísticas y actualizarlo
        Component[] comps = ((JPanel)filtrosPanel.getParent().getComponent(1)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().contains(" ")) {
                ((JLabel)comp).setText(String.format(" Total: %d | Activos: %d | Inactivos: %d",
                        resultado.size(), activos, inactivos));
                break;
            }
        }

        // Tooltip con resultados
        tabla.getTableHeader().setToolTipText(
                resultado.size() + " resultado(s) de " + listaVendedores.size() + " total"
        );
    }

    /**
     * Verifica si el vendedor coincide con la búsqueda
     */
    private boolean coincideBusqueda(Vendedor v, String busqueda, String filtro) {
        return switch (filtro) {
            case "Por Nombre" -> (v.getNombre() + " " + v.getApellido()).toLowerCase().contains(busqueda);
            case "Por Usuario" -> v.getUsuario() != null && v.getUsuario().toLowerCase().contains(busqueda);
            case "Por Email" -> v.getEmail() != null && v.getEmail().toLowerCase().contains(busqueda);
            default -> // "Todos"
                    (v.getNombre() + " " + v.getApellido()).toLowerCase().contains(busqueda) ||
                            (v.getUsuario() != null && v.getUsuario().toLowerCase().contains(busqueda)) ||
                            (v.getEmail() != null && v.getEmail().toLowerCase().contains(busqueda));
        };
    }

    /**
     * Limpia los filtros adicionales
     */
    private void limpiarFiltros() {
        cbFiltroRol.setSelectedIndex(0);
        cbFiltroEstado.setSelectedIndex(0);
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

    private void cargarVendedores() {
        setBotonesEnabled(false);
        modeloTabla.setRowCount(0);

        SwingWorker<List<Vendedor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vendedor> doInBackground() throws Exception {
                return controlador.listarVendedores();
            }

            @Override
            protected void done() {
                try {
                    listaVendedores = get();
                    actualizarTabla(listaVendedores);
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(VendedoresPanel.this,
                            "Error al cargar vendedores: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void actualizarTabla(List<Vendedor> vendedores) {
        modeloTabla.setRowCount(0);
        for (Vendedor v : vendedores) {
            modeloTabla.addRow(new Object[]{
                    v.getIdVendedor(),
                    v.getNombre() + " " + v.getApellido(),
                    v.getUsuario(),
                    v.getRol(),
                    v.getEmail(),
                    v.getTelefono(),
                    String.format("%.1f%%", v.getComisionPorcentaje()),
                    v.isActivo() ? "✓ Sí" : "✗ No"
            });
        }
    }

    private void setBotonesEnabled(boolean enabled) {
        btnAgregar.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
        btnActivar.setEnabled(enabled);
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
            JOptionPane.showMessageDialog(this,
                    "Seleccione un vendedor para editar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int idVendedor = (int) modeloTabla.getValueAt(fila, 0);
        listaVendedores.stream()
                .filter(vendedor -> vendedor.getIdVendedor() == idVendedor)
                .findFirst().ifPresent(this::abrirFormulario);

    }

    private void toggleActivo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un vendedor para activar o desactivar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int idVendedor = (int) modeloTabla.getValueAt(fila, 0);
        Vendedor v = listaVendedores.stream()
                .filter(vendedor -> vendedor.getIdVendedor() == idVendedor)
                .findFirst()
                .orElse(null);

        if (v != null) {
            String accion = v.isActivo() ? "desactivar" : "activar";
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea " + accion + " a " + v.getNombre() + " " + v.getApellido() + "?",
                    "Confirmar Acción",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                v.setActivo(!v.isActivo());

                if (controlador.actualizarVendedor(v)) {
                    JOptionPane.showMessageDialog(this,
                            "Estado del vendedor actualizado correctamente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarVendedores();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el estado del vendedor.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}