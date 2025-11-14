package vista;

import controlador.ClienteControlador;
import modelo.Cliente;
import util.FiltrosPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Panel para el CRUD (Gestión) de Clientes.
 */
public class ClientesPanel extends JPanel {
    private final ClienteControlador controlador;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private List<Cliente> listaClientes;
    private final JButton btnAgregar;
    private final JButton btnEditar;
    private TableRowSorter<DefaultTableModel> sorter;
    private final FiltrosPanel filtrosPanel;

    public ClientesPanel() {
        controlador = new ClienteControlador();
        setLayout(new BorderLayout(0, 10));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("👥 Gestión de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // ===== PANEL DE BÚSQUEDA =====
        String[] filtros = {"Todos", "Por Nombre", "Por Email", "Por RFC", "Por Teléfono"};
        filtrosPanel = new FiltrosPanel("Buscar cliente...", filtros);
        filtrosPanel.setBusquedaTiempoReal(true);
        filtrosPanel.setOnSearch(this::buscarCliente);
        filtrosPanel.setOnFilterChange(filtro -> buscarCliente(filtrosPanel.getTextoBusqueda()));

        // Panel contenedor para búsqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(filtrosPanel, BorderLayout.NORTH);

        // ===== TABLA =====
        String[] columnas = {"ID", "Nombre Completo", "Email", "Teléfono", "RFC", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar ordenamiento
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelSuperior.add(scrollPane, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        btnAgregar = crearBoton("Agregar Cliente", new Color(46, 204, 113));
        btnEditar = crearBoton("✏Editar Seleccionado", new Color(52, 152, 219));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(149, 165, 166));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        // ===== ACCIONES =====
        btnAgregar.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnRefrescar.addActionListener(e -> {
            filtrosPanel.limpiarBusqueda();
            cargarClientes();
        });

        // Doble click para editar
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirFormularioEditar();
                }
            }
        });

        // Carga inicial de datos
        cargarClientes();
    }

    /**
     * Crea un botón estilizado
     */
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

    /**
     * Carga todos los clientes en la tabla
     */
    private void cargarClientes() {
        setBotonesEnabled(false);
        modeloTabla.setRowCount(0);

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return controlador.listarClientes();
            }

            @Override
            protected void done() {
                try {
                    listaClientes = get();
                    actualizarTabla(listaClientes);
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(ClientesPanel.this,
                            "Error al cargar clientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setBotonesEnabled(true);
                }
            }
        };
        worker.execute();
    }

    /**
     * Actualiza la tabla con una lista de clientes
     */
    private void actualizarTabla(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente c : clientes) {
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

    /**
     * Busca clientes según el texto y filtro
     */
    private void buscarCliente(String textoBusqueda) {
        if (listaClientes == null || listaClientes.isEmpty()) {
            return;
        }

        if (textoBusqueda == null || textoBusqueda.isEmpty()) {
            // Mostrar todos
            actualizarTabla(listaClientes);
            return;
        }

        String filtro = filtrosPanel.getFiltroSeleccionado();
        String busqueda = textoBusqueda.toLowerCase();

        List<Cliente> resultado = listaClientes.stream()
                .filter(c -> {
                    switch (filtro) {
                        case "Por Nombre":
                            return (c.getNombre() + " " + c.getApellido()).toLowerCase().contains(busqueda);

                        case "Por Email":
                            return c.getEmail() != null && c.getEmail().toLowerCase().contains(busqueda);

                        case "Por RFC":
                            return c.getRfc() != null && c.getRfc().toLowerCase().contains(busqueda);

                        case "Por Teléfono":
                            return c.getTelefono() != null && c.getTelefono().contains(busqueda);

                        default: // "Todos"
                            return (c.getNombre() + " " + c.getApellido()).toLowerCase().contains(busqueda) ||
                                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(busqueda)) ||
                                    (c.getRfc() != null && c.getRfc().toLowerCase().contains(busqueda)) ||
                                    (c.getTelefono() != null && c.getTelefono().contains(busqueda));
                    }
                })
                .toList();

        actualizarTabla(resultado);

        // Mostrar contador de resultados
        tabla.getTableHeader().setToolTipText(
                resultado.size() + " resultado(s) de " + listaClientes.size() + " total"
        );
    }

    private void setBotonesEnabled(boolean enabled) {
        btnAgregar.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
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
            JOptionPane.showMessageDialog(this,
                    "Seleccione un cliente para editar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener el ID real (la tabla puede estar ordenada/filtrada)
        int filaModelo = tabla.convertRowIndexToModel(fila);
        int idCliente = (int) modeloTabla.getValueAt(filaModelo, 0);

        Cliente c = listaClientes.stream()
                .filter(cliente -> cliente.getIdCliente() == idCliente)
                .findFirst()
                .orElse(null);

        if (c != null) {
            abrirFormulario(c);
        }
    }
}