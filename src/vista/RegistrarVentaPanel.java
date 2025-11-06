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
 */
public class RegistrarVentaPanel extends JPanel {

    private final Vendedor vendedor; // El vendedor que está logueado

    // Controladores
    private final VentaControlador ventaControlador;
    private final VehiculoControlador vehiculoControlador;
    private final ClienteControlador clienteControlador;
    private final MetodoPagoControlador metodoPagoControlador;

    // Componentes del Formulario
    private JComboBox<Cliente> cbClientes;
    private JComboBox<Vehiculo> cbVehiculos;
    private JComboBox<MetodoPago> cbMetodosPago;
    private JTextField txtPrecioFinal;
    private JLabel lblVehiculoInfo; // Label para mostrar detalles del auto

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

        add(panelFormulario, BorderLayout.NORTH);

        // --- Botón de Guardar ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardarVenta = new JButton("REGISTRAR VENTA");
        btnGuardarVenta.setFont(new Font("Arial", Font.BOLD, 18));
        btnGuardarVenta.setBackground(new Color(0, 153, 51)); // Verde
        btnGuardarVenta.setForeground(Color.WHITE);
        panelBoton.add(btnGuardarVenta);
        add(panelBoton, BorderLayout.CENTER);

        // --- Lógica de Carga y Acciones ---
        cargarComboBoxes();

        // Listener para mostrar info del vehículo
        cbVehiculos.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Vehiculo v = (Vehiculo) cbVehiculos.getSelectedItem();
                if (v != null) {
                    lblVehiculoInfo.setText(String.format("Año: %d, Color: %s, Km: %d, Precio Lista: $%.2f",
                            v.getAnio(), v.getColor(), v.getKilometraje(), v.getPrecio()));
                    // Poner el precio de lista como sugerencia
                    txtPrecioFinal.setText(String.format("%.2f", v.getPrecio()));
                }
            }
        });

        btnGuardarVenta.addActionListener(e -> registrarVenta());
    }

    private void cargarComboBoxes() {
        // Clientes
        cbClientes.removeAllItems();
        List<Cliente> clientes = clienteControlador.listarClientes();
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    setText(((Cliente) value).getNombre() + " " + ((Cliente) value).getApellido());
                }
                return this;
            }
        });
        for (Cliente c : clientes) {
            cbClientes.addItem(c);
        }

        // Vehículos
        cbVehiculos.removeAllItems();
        List<Vehiculo> vehiculos = vehiculoControlador.listarVehiculosDisponibles();
        cbVehiculos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vehiculo) {
                    setText(((Vehiculo) value).getNombreMarca() + " " + ((Vehiculo) value).getNombreModelo() + " (VIN: ..." + ((Vehiculo) value).getNumeroSerie().substring(12) + ")");
                }
                return this;
            }
        });
        for (Vehiculo v : vehiculos) {
            cbVehiculos.addItem(v);
        }

        // Métodos de Pago
        cbMetodosPago.removeAllItems();
        List<MetodoPago> metodos = metodoPagoControlador.listarActivos();
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

            if (cliente == null || vehiculo == null || metodo == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente, vehículo y método de pago.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Crear objeto Venta
            Venta v = new Venta();
            v.setIdCliente(cliente.getIdCliente());
            v.setIdVehiculo(vehiculo.getIdVehiculo());
            v.setIdVendedor(vendedor.getIdVendedor()); // El vendedor logueado
            v.setIdMetodoPago(metodo.getIdMetodoPago());
            v.setFechaVenta(new Date(System.currentTimeMillis())); // Fecha de hoy
            v.setPrecioFinal(precioFinal);

            // (Añadir lógica de financiamiento si es necesario)

            // 3. Confirmar
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar la venta de:\n" +
                            "Vehículo: " + vehiculo.getNombreMarca() + " " + vehiculo.getNombreModelo() + "\n" +
                            "Cliente: " + cliente.getNombre() + " " + cliente.getApellido() + "\n" +
                            "Precio Final: $" + precioFinal + "\n\n" +
                            "Esta acción marcará el vehículo como 'vendido' y es irreversible.",
                    "Confirmar Venta", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 4. Registrar
                if (ventaControlador.registrarVenta(v)) {
                    JOptionPane.showMessageDialog(this, "¡Venta registrada exitosamente!");
                    // 5. Refrescar
                    cargarComboBoxes();
                    txtPrecioFinal.setText("");
                    lblVehiculoInfo.setText("Seleccione un vehículo...");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar la venta (transacción fallida).", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio final debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}
