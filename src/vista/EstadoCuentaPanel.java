package vista;

import controlador.PagoControlador;
import controlador.VentaControlador;
import modelo.Pago;
import modelo.Venta;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

/**
 * Panel para la Gestión de Pagos (Estados de Cuenta).
 */
public class EstadoCuentaPanel extends JPanel {

    private final VentaControlador ventaControlador;
    private final PagoControlador pagoControlador;

    private final JComboBox<Venta> cbVentasFinanciadas;
    private final JTable tablaPagos;
    private final DefaultTableModel modeloTabla;
    private final JButton btnRegistrarPago;
    private List<Pago> listaPagosActual;

    // Labels de resumen
    private JLabel lblMontoTotal;
    private JLabel lblPagado;
    private JLabel lblPendiente;
    private JLabel lblProximoPago;

    public EstadoCuentaPanel() {
        this.ventaControlador = new VentaControlador();
        this.pagoControlador = new PagoControlador();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel(" Gestión de Pagos y Financiamiento", CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // ===== PANEL CENTRAL =====
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

        // Panel superior: Selección de venta + Resumen
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        // Selección de venta
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelSeleccion.setBackground(new Color(236, 240, 241));
        panelSeleccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblSeleccionar = new JLabel("Seleccionar Financiamiento:");
        lblSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelSeleccion.add(lblSeleccionar);

        cbVentasFinanciadas = new JComboBox<>();
        cbVentasFinanciadas.setPreferredSize(new Dimension(400, 35));
        cbVentasFinanciadas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelSeleccion.add(cbVentasFinanciadas);

        panelSuperior.add(panelSeleccion, BorderLayout.NORTH);

        // Panel de resumen financiero
        JPanel panelResumen = crearPanelResumen();
        panelSuperior.add(panelResumen, BorderLayout.CENTER);

        panelCentral.add(panelSuperior, BorderLayout.NORTH);

        // ===== TABLA DE PAGOS =====
        String[] columnas = {"#", "Concepto", "Monto", "Vencimiento", "Estado", "Fecha Pago"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Double.class; // Monto
                return String.class;
            }
        };

        tablaPagos = new JTable(modeloTabla);
        configurarTabla();

        JScrollPane scrollPane = new JScrollPane(tablaPagos);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                "Cronograma de Pagos",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        panelCentral.add(scrollPane, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // ===== PANEL INFERIOR (BOTONES) =====
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelSur.setBackground(new Color(236, 240, 241));
        panelSur.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        btnRegistrarPago = crearBoton("✓ Registrar Pago de Mensualidad", new Color(46, 204, 113));
        JButton btnRefrescar = crearBoton(" Refrescar", new Color(149, 165, 166));

        btnRefrescar.addActionListener(e -> {
            Venta v = (Venta) cbVentasFinanciadas.getSelectedItem();
            if (v != null) {
                cargarPagos(v.getIdVenta());
            }
        });

        panelSur.add(btnRefrescar);
        panelSur.add(btnRegistrarPago);
        add(panelSur, BorderLayout.SOUTH);

        // ===== LÓGICA Y ACCIONES =====
        cargarVentasFinanciadas();

        cbVentasFinanciadas.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Venta v = (Venta) cbVentasFinanciadas.getSelectedItem();
                if (v != null) {
                    cargarPagos(v.getIdVenta());
                }
            }
        });

        btnRegistrarPago.addActionListener(e -> registrarPagoSeleccionado());
    }

    /**
     * Crea panel con resumen financiero
     */
    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblMontoTotal = new JLabel();
        lblPagado = new JLabel();
        lblPendiente = new JLabel();
        lblProximoPago = new JLabel();

        panel.add(crearTarjetaResumen("Monto Total", "$0.00", new Color(52, 152, 219), lblMontoTotal));
        panel.add(crearTarjetaResumen("Pagado", "$0.00", new Color(46, 204, 113), lblPagado));
        panel.add(crearTarjetaResumen("Pendiente", "$0.00", new Color(231, 76, 60), lblPendiente));
        panel.add(crearTarjetaResumen("Próximo Pago", "N/A", new Color(241, 196, 15), lblProximoPago));

        return panel;
    }

    private JPanel crearTarjetaResumen(String titulo, String valorInicial, Color color, JLabel lblValor) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo, CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);

        lblValor.setText(valorInicial);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(Color.WHITE);
        lblValor.setHorizontalAlignment(CENTER);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Configura la apariencia de la tabla
     */
    private void configurarTabla() {
        // Tamaños de columnas
        tablaPagos.getColumnModel().getColumn(0).setMaxWidth(50);  // #
        tablaPagos.getColumnModel().getColumn(2).setMaxWidth(120); // Monto
        tablaPagos.getColumnModel().getColumn(3).setMaxWidth(120); // Vencimiento
        tablaPagos.getColumnModel().getColumn(4).setMaxWidth(100); // Estado
        tablaPagos.getColumnModel().getColumn(5).setMaxWidth(120); // Fecha Pago

        // Altura de filas
        tablaPagos.setRowHeight(35);

        // Fuentes
        tablaPagos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTableHeader header = tablaPagos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Renderizador personalizado para la columna de Estado
        tablaPagos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String estado = value.toString().toLowerCase();
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (estado.contains("pagado")) {
                    setBackground(new Color(46, 204, 113));
                    setForeground(Color.WHITE);
                    setText("✓ PAGADO");
                } else if (estado.contains("pendiente")) {
                    setBackground(new Color(231, 76, 60));
                    setForeground(Color.WHITE);
                    setText("⏱ PENDIENTE");
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                if (isSelected) {
                    setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
                }

                return c;
            }
        });

        // Renderizador para la columna de Monto
        tablaPagos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 13));

                if (!isSelected) {
                    setBackground(new Color(236, 240, 241));
                }

                return c;
            }
        });

        // Centrar otras columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(CENTER);
        tablaPagos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaPagos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tablaPagos.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Alternar colores de filas
        tablaPagos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // No aplicar a columnas con renderizadores personalizados
                if (column != 2 && column != 4) {
                    if (!isSelected) {
                        if (row % 2 == 0) {
                            c.setBackground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(245, 246, 247));
                        }
                    }
                }

                return c;
            }
        });
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

    private void cargarVentasFinanciadas() {
        cbVentasFinanciadas.setEnabled(false);

        SwingWorker<List<Venta>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                return ventaControlador.listarVentasConFinanciamiento();
            }

            @Override
            protected void done() {
                try {
                    List<Venta> ventas = get();
                    cbVentasFinanciadas.removeAllItems();

                    cbVentasFinanciadas.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                      int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Venta v) {
                                setText(String.format(" %s - %d meses ($%.2f)",
                                        v.getNombreCliente(), v.getPlazoMeses(), v.getPrecioFinal()));
                            }
                            return this;
                        }
                    });

                    for (Venta v : ventas) {
                        cbVentasFinanciadas.addItem(v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cbVentasFinanciadas.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void cargarPagos(int idVenta) {
        btnRegistrarPago.setEnabled(false);
        modeloTabla.setRowCount(0);

        SwingWorker<List<Pago>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pago> doInBackground() throws Exception {
                return pagoControlador.listarPagosPorVenta(idVenta);
            }

            @Override
            protected void done() {
                try {
                    listaPagosActual = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    double totalPagado = 0;
                    double totalPendiente = 0;
                    String proximaFecha = "N/A";

                    for (Pago p : listaPagosActual) {
                        String numPago = p.getNumeroPago() == 0 ? "Enganche" : String.valueOf(p.getNumeroPago());
                        String vencimiento = p.getFechaVencimiento() != null ?
                                sdf.format(p.getFechaVencimiento()) : "-";
                        String fechaPago = p.getFechaPago() != null ?
                                sdf.format(p.getFechaPago()) : "-";

                        modeloTabla.addRow(new Object[]{
                                numPago,
                                p.getConcepto(),
                                String.format("$%.2f", p.getMonto()),
                                vencimiento,
                                p.getEstado(),
                                fechaPago
                        });

                        if (p.getEstado().equalsIgnoreCase("pagado")) {
                            totalPagado += p.getMonto();
                        } else {
                            totalPendiente += p.getMonto();
                            if (proximaFecha.equals("N/A") && p.getFechaVencimiento() != null) {
                                proximaFecha = sdf.format(p.getFechaVencimiento());
                            }
                        }
                    }

                    // Actualizar resumen
                    double montoTotal = totalPagado + totalPendiente;
                    lblMontoTotal.setText(String.format("$%.2f", montoTotal));
                    lblPagado.setText(String.format("$%.2f", totalPagado));
                    lblPendiente.setText(String.format("$%.2f", totalPendiente));
                    lblProximoPago.setText(proximaFecha);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    btnRegistrarPago.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void registrarPagoSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una mensualidad de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pago pagoSeleccionado = listaPagosActual.get(fila);

        if (pagoSeleccionado.getEstado().equalsIgnoreCase("pagado")) {
            JOptionPane.showMessageDialog(this,
                    "Esta mensualidad ya fue pagada.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (pagoSeleccionado.getEstado().equalsIgnoreCase("pendiente")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("¿Confirmar el pago de:\n\n%s\nMonto: $%.2f\n",
                            pagoSeleccionado.getConcepto(),
                            pagoSeleccionado.getMonto()),
                    "Confirmar Pago",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (pagoControlador.pagarMensualidad(pagoSeleccionado.getIdPago())) {
                    JOptionPane.showMessageDialog(this,
                            "✓ Pago registrado exitosamente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    Venta v = (Venta) cbVentasFinanciadas.getSelectedItem();
                    if (v != null) {
                        cargarPagos(v.getIdVenta());
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al registrar el pago.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}