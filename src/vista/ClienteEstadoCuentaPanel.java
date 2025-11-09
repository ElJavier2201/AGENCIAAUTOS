package vista;

import controlador.PagoControlador;
import controlador.VentaControlador;
import modelo.Cliente;
import modelo.Pago;
import modelo.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Panel para que el CLIENTE vea su estado de cuenta.
 */
public class ClienteEstadoCuentaPanel extends JPanel {

    private final VentaControlador ventaControlador;
    private final PagoControlador pagoControlador;
    private final Cliente clienteLogueado; // El cliente que inició sesión

    private final JComboBox<Venta> cbMisVentas;
    private final JTable tablaPagos;
    private final DefaultTableModel modeloTabla;
    private List<Pago> listaPagosActual;

    public ClienteEstadoCuentaPanel(Cliente cliente) {
        this.clienteLogueado = cliente;
        this.ventaControlador = new VentaControlador();
        this.pagoControlador = new PagoControlador();
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior (Selección de Venta) ---
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.setBorder(BorderFactory.createTitledBorder("Seleccionar Vehículo Financiado"));
        cbMisVentas = new JComboBox<>();
        panelNorte.add(new JLabel("Mis Compras:"));
        panelNorte.add(cbMisVentas);
        add(panelNorte, BorderLayout.NORTH);

        // --- Panel Central (Tabla de Pagos) ---
        String[] columnas = {"Num. Pago", "Concepto", "Monto", "Fecha Vencimiento", "Estado", "Fecha de Pago"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPagos = new JTable(modeloTabla);
        add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        // --- Lógica y Acciones ---
        cargarVentasFinanciadas();

        cbMisVentas.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Venta v = (Venta) cbMisVentas.getSelectedItem();
                if (v != null) {
                    cargarPagos(v.getIdVenta());
                }
            }
        });
    }

    /**
     * Carga el ComboBox con las ventas del cliente logueado.
     */
    private void cargarVentasFinanciadas() {
        cbMisVentas.setEnabled(false);

        SwingWorker<List<Venta>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                // --- CAMBIO CLAVE: Llama al nuevo método del controlador ---
                return ventaControlador.listarVentasConFinanciamiento(clienteLogueado.getIdCliente());
            }

            @Override
            protected void done() {
                try {
                    List<Venta> ventas = get();
                    cbMisVentas.removeAllItems();

                    cbMisVentas.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Venta) {
                                Venta v = (Venta) value;
                                // (Necesitamos cargar el nombre del vehículo aquí,
                                // por ahora solo mostramos el ID de venta)
                                setText("Venta ID: " + v.getIdVenta() + " (" + v.getPlazoMeses() + " meses)");
                            }
                            return this;
                        }
                    });

                    for (Venta v : ventas) {
                        cbMisVentas.addItem(v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cbMisVentas.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    /**
     * Carga la tabla con los pagos de la venta seleccionada.
     */
    private void cargarPagos(int idVenta) {
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
                    for (Pago p : listaPagosActual) {
                        modeloTabla.addRow(new Object[]{
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
                }
            }
        };
        worker.execute();
    }
}