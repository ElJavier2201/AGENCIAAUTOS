package vista;
import controlador.*;
import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.Date;
import java.util.List;

/**
 * Panel (JPanel) que contiene el formulario para registrar una nueva venta.
 *
 */
public class RegistrarVentaPanel extends JPanel {

    private final Vendedor vendedor;

    // Controladores
    private final VentaControlador ventaControlador;
    private final VehiculoControlador vehiculoControlador;
    private final ClienteControlador clienteControlador;
    private final MetodoPagoControlador metodoPagoControlador;

    // Componentes del Formulario
    private final JComboBox<Cliente> cbClientes;
    private final JComboBox<Vehiculo> cbVehiculos;
    private final JComboBox<MetodoPago> cbMetodosPago;
    private final JTextField txtPrecioFinal;
    private final JLabel lblVehiculoInfo;

    // --- NUEVO: Componentes de Financiamiento ---
    private JPanel panelFinanciamiento; // Un panel para agruparlos
    private JTextField txtEnganche;
    private JTextField txtPlazoMeses;
    private JTextField txtTasaInteres;

    public RegistrarVentaPanel(Vendedor vendedor) {
        this.vendedor = vendedor;
        this.ventaControlador = new VentaControlador();
        this.vehiculoControlador = new VehiculoControlador();
        this.clienteControlador = new ClienteControlador();
        this.metodoPagoControlador = new MetodoPagoControlador();

        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Fila 1: Cliente ---
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbClientes = new JComboBox<>();
        panelFormulario.add(cbClientes, gbc);

        // --- Fila 2: Vehículo ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Vehículo (Disponibles):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbVehiculos = new JComboBox<>();
        panelFormulario.add(cbVehiculos, gbc);

        // --- Fila 3: Info Vehículo ---
        gbc.gridx = 1; gbc.gridy = 2;
        lblVehiculoInfo = new JLabel("Seleccione un vehículo...");
        lblVehiculoInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        panelFormulario.add(lblVehiculoInfo, gbc);

        // --- Fila 4: Método de Pago ---
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Método de Pago:"), gbc);
        gbc.gridx = 1;
        cbMetodosPago = new JComboBox<>();
        panelFormulario.add(cbMetodosPago, gbc);

        // --- Fila 5: Precio Final ---
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Precio Final (Acordado):"), gbc);
        gbc.gridx = 1;
        txtPrecioFinal = new JTextField();
        panelFormulario.add(txtPrecioFinal, gbc);

        // --- NUEVO: Fila 6: Panel de Financiamiento ---
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2; // Ocupa ambas columnas
        crearPanelFinanciamiento(); // Llama al helper
        panelFormulario.add(panelFinanciamiento, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // --- Botón de Guardar (Sin cambios) ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardarVenta = new JButton("REGISTRAR VENTA");
        // ... (resto del botón)
        panelBoton.add(btnGuardarVenta);
        add(panelBoton, BorderLayout.CENTER);

        // --- Lógica de Carga y Acciones ---
        cargarComboBoxes();

        // Listener para info del vehículo
        cbVehiculos.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Vehiculo v = (Vehiculo) cbVehiculos.getSelectedItem();
                if (v != null) {
                    lblVehiculoInfo.setText(String.format("Año: %d, Color: %s, Km: %d, Precio Lista: $%.2f",
                            v.getAnio(), v.getColor(), v.getKilometraje(), v.getPrecio()));
                    txtPrecioFinal.setText(String.format("%.2f", v.getPrecio()));
                }
            }
        });

        // --- NUEVO: Listener para Método de Pago ---
        cbMetodosPago.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                MetodoPago mp = (MetodoPago) cbMetodosPago.getSelectedItem();
                if (mp != null) {
                    // Activa o desactiva el panel de financiamiento
                    setFinanciamientoEnabled(mp.isRequiereFinanciamiento());
                }
            }
        });

        btnGuardarVenta.addActionListener(e -> registrarVenta());

        // Desactivar financiamiento al inicio
        setFinanciamientoEnabled(false);
    }

    /**
     * --- NUEVO: Helper para crear el panel de financiamiento ---
     */
    private void crearPanelFinanciamiento() {
        panelFinanciamiento = new JPanel(new GridLayout(1, 6, 10, 0));
        panelFinanciamiento.setBorder(BorderFactory.createTitledBorder("Datos de Financiamiento"));

        txtEnganche = new JTextField("0");
        txtPlazoMeses = new JTextField("0");
        txtTasaInteres = new JTextField("0.0");

        panelFinanciamiento.add(new JLabel("Enganche:"));
        panelFinanciamiento.add(txtEnganche);
        panelFinanciamiento.add(new JLabel("Plazo (Meses):"));
        panelFinanciamiento.add(txtPlazoMeses);
        panelFinanciamiento.add(new JLabel("Tasa Interés (%):"));
        panelFinanciamiento.add(txtTasaInteres);
    }

    /**
     * --- NUEVO: Helper para activar/desactivar el panel ---
     */
    private void setFinanciamientoEnabled(boolean enabled) {
        panelFinanciamiento.setVisible(enabled);

        // Habilitar/deshabilitar los textfields internos
        for (Component comp : panelFinanciamiento.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setEditable(enabled);
            }
        }

        // Si se desactiva, limpiar valores
        if (!enabled) {
            txtEnganche.setText("0");
            txtPlazoMeses.setText("0");
            txtTasaInteres.setText("0.0");
        }
    }

    // --- (cargarComboBoxes sin cambios) ---
    private void cargarComboBoxes() {
        // Clientes
        cbClientes.removeAllItems();
        List<Cliente> clientes = clienteControlador.listarClientes();
        cbClientes.setRenderer(new DefaultListCellRenderer() { /* ... */ });
        for (Cliente c : clientes) { cbClientes.addItem(c); }

        // Vehículos
        cbVehiculos.removeAllItems();
        List<Vehiculo> vehiculos = vehiculoControlador.listarVehiculosDisponibles();
        cbVehiculos.setRenderer(new DefaultListCellRenderer() { /* ... */ });
        for (Vehiculo v : vehiculos) { cbVehiculos.addItem(v); }

        // Métodos de Pago
        cbMetodosPago.removeAllItems();
        List<MetodoPago> metodos = metodoPagoControlador.listarActivos();
        // cbMetodosPago.setRenderer(...) // (Ya usa .toString() que está bien)
        for (MetodoPago mp : metodos) {
            cbMetodosPago.addItem(mp);
        }
    }

    private void registrarVenta() {
        try {
            // 1. Recolectar datos
            Cliente cliente = (Cliente) cbClientes.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cbVehiculos.getSelectedItem();
            MetodoPago metodo = (MetodoPago) cbMetodosPago.getSelectedItem();
            double precioFinal = Double.parseDouble(txtPrecioFinal.getText());

            double enganche = Double.parseDouble(txtEnganche.getText());
            int plazoMeses = Integer.parseInt(txtPlazoMeses.getText());
            double tasaInteres = Double.parseDouble(txtTasaInteres.getText());

            if (cliente == null || vehiculo == null || metodo == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente, vehículo y método de pago.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Crear objeto Venta
            Venta v = new Venta();
            v.setIdCliente(cliente.getIdCliente());
            v.setIdVehiculo(vehiculo.getIdVehiculo());
            v.setIdVendedor(vendedor.getIdVendedor());
            v.setIdMetodoPago(metodo.getIdMetodoPago());
            v.setFechaVenta(new Date(System.currentTimeMillis()));
            v.setPrecioFinal(precioFinal);
            v.setEnganche(enganche);
            v.setPlazoMeses(plazoMeses);
            v.setTasaInteres(tasaInteres);

            // 3. Confirmar (igual que antes)
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar la venta de:\n" +
                            "Vehículo: " + vehiculo.getNombreMarca() + " " + vehiculo.getNombreModelo() + "\n" +
                            "Precio Final: $" + precioFinal + "\n\n" +
                            (plazoMeses > 0 ? "Enganche: $" + enganche : "Pago de Contado"),
                    "Confirmar Venta", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                // 4. Registrar Venta
                int idVentaGenerada = ventaControlador.registrarVenta(v);

                if (idVentaGenerada > 0) {
                    JOptionPane.showMessageDialog(this, "¡Venta registrada exitosamente! ID: " + idVentaGenerada);

                    // 5. Registrar Pago Inicial (Enganche o Contado)
                    PagoControlador pc = new PagoControlador();
                    if (pc.registrarPagoInicial(v, metodo)) {
                        JOptionPane.showMessageDialog(this, "Pago inicial (o de contado) registrado.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al registrar el pago inicial.", "Error de Pago", JOptionPane.ERROR_MESSAGE);
                    }

                    // --- 6. GENERAR MENSUALIDADES (NUEVO) ---
                    if (metodo.isRequiereFinanciamiento()) {
                        if (pc.generarMensualidadesPendientes(v)) {
                            JOptionPane.showMessageDialog(this,
                                    "Plan de financiamiento de " + v.getPlazoMeses() + " mensualidades generado.",
                                    "Financiamiento Creado", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Error al generar el plan de financiamiento.", "Error de Pagos", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    // 7. PREGUNTAR POR FACTURA
                    int confirmFactura = JOptionPane.showConfirmDialog(this,
                            "Venta guardada. ¿Desea ingresar los datos fiscales para la factura ahora?",
                            "Facturación", JOptionPane.YES_NO_OPTION);

                    if (confirmFactura == JOptionPane.YES_OPTION) {
                        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
                        FacturaFormDialog dialog = new FacturaFormDialog(owner, v, cliente);
                        dialog.setVisible(true);

                        if (dialog.isGuardado()) {
                            JOptionPane.showMessageDialog(this, "Factura generada y guardada.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Factura no generada.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    // 8. Refrescar
                    cargarComboBoxes();
                    txtPrecioFinal.setText("");
                    lblVehiculoInfo.setText("Seleccione un vehículo...");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar la venta (transacción fallida).", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio final y los campos de financiamiento deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}