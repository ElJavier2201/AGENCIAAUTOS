package vista;

import controlador.PagoControlador;
import controlador.VentaControlador;
import modelo.Pago;
import modelo.Venta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private List<Pago> listaPagosActual; // Cache de los pagos

    public EstadoCuentaPanel() {
        this.ventaControlador = new VentaControlador();
        this.pagoControlador = new PagoControlador();
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior (Selección de Venta) ---
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.setBorder(BorderFactory.createTitledBorder("Seleccionar Venta con Financiamiento"));
        cbVentasFinanciadas = new JComboBox<>();
        panelNorte.add(new JLabel("Venta:"));
        panelNorte.add(cbVentasFinanciadas);
        add(panelNorte, BorderLayout.NORTH);

        // --- Panel Central (Tabla de Pagos) ---
        String[] columnas = {"ID Pago", "Num. Pago", "Concepto", "Monto", "Fecha Vencimiento", "Estado", "Fecha de Pago"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPagos = new JTable(modeloTabla);
        add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        // --- Panel Sur (Botones) ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrarPago = new JButton("Registrar Pago de Mensualidad");
        panelSur.add(btnRegistrarPago);
        add(panelSur, BorderLayout.SOUTH);

        // --- Lógica y Acciones ---
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
     * Carga el ComboBox con las ventas que tienen plazos > 0.
     * (Usa SwingWorker)
     */
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

                    // Definir cómo se ve la Venta en el ComboBox
                    cbVentasFinanciadas.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Venta v) {
                                setText(v.getNombreCliente() + " (Venta ID: " + v.getIdVenta() + " - " + v.getPlazoMeses() + " meses)");
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

    /**
     * Carga la tabla con los pagos de la venta seleccionada.
     * (Usa SwingWorker)
     */
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
                    listaPagosActual = get(); // Guardar en caché
                    for (Pago p : listaPagosActual) {
                        modeloTabla.addRow(new Object[]{
                                p.getIdPago(),
                                p.getNumeroPago() == 0 ? "Enganche" : p.getNumeroPago(),
                                p.getConcepto(),
                                String.format("%.2f", p.getMonto()),
                                p.getFechaVencimiento(),
                                p.getEstado(),
                                p.getFechaPago()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    btnRegistrarPago.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    /**
     * Marca como 'pagada' la mensualidad seleccionada en la tabla.
     */
    private void registrarPagoSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una mensualidad de la tabla.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el objeto Pago de nuestro caché
        Pago pagoSeleccionado = listaPagosActual.get(fila);

        if (pagoSeleccionado.getEstado().equalsIgnoreCase("pagado")) {
            JOptionPane.showMessageDialog(this, "Esta mensualidad ya fue pagada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (pagoSeleccionado.getEstado().equalsIgnoreCase("pendiente")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar el pago de la mensualidad:\n" +
                            "Concepto: " + pagoSeleccionado.getConcepto() + "\n" +
                            "Monto: $" + pagoSeleccionado.getMonto() + "\n",
                    "Confirmar Pago", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (pagoControlador.pagarMensualidad(pagoSeleccionado.getIdPago())) {
                    JOptionPane.showMessageDialog(this, "Pago registrado exitosamente.");
                    // Refrescar la tabla
                    Venta v = (Venta) cbVentasFinanciadas.getSelectedItem();
                    if (v != null) {
                        cargarPagos(v.getIdVenta());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el pago.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
