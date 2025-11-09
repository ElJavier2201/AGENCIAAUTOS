package vista;

import controlador.FacturaControlador;
import modelo.Cliente;
import modelo.Factura;
import modelo.Venta;
import javax.swing.*;
import java.awt.*;
        import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * JDialog para capturar los datos fiscales de una nueva factura.
 */
public class FacturaFormDialog extends JDialog {

    private final FacturaControlador controlador;
    private final Venta venta; // La venta que estamos facturando
    private final Cliente cliente; // El cliente para pre-llenar el RFC

    // Componentes del formulario
    private JTextField txtRFC, txtLugarExpedicion, txtNumeroFactura;
    private JTextField txtSubtotal, txtIVA, txtTotal;
    private JComboBox<String> cbUsoCFDI, cbMetodoPagoSAT, cbFormaPagoSAT;

    private boolean guardado = false;

    // (Podrías usar Mapas para catálogos del SAT, pero usamos arrays por simplicidad)
    private final String[] usosCFDI = {"G01-Adquisición de mercancías", "G03-Gastos en general", "I08-Mobiliario y equipo de oficina"};
    private final String[] formasPagoSAT = {"01-Efectivo", "03-Transferencia", "04-Tarjeta de crédito", "28-Tarjeta de débito"};
    private final String[] metodosPagoSAT = {"PUE-Pago en una sola exhibición", "PPD-Pago en parcialidades o diferido"};

    public FacturaFormDialog(Frame owner, Venta venta, Cliente cliente) {
        super(owner, "Generar Factura (CFDI)", true);
        this.controlador = new FacturaControlador();
        this.venta = venta;
        this.cliente = cliente;

        setSize(550, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        precargarDatos();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos (No editables, solo informativos)
        txtSubtotal = new JTextField();
        txtSubtotal.setEditable(false);
        txtIVA = new JTextField();
        txtIVA.setEditable(false);
        txtTotal = new JTextField();
        txtTotal.setEditable(false);

        // Campos (Editables)
        txtRFC = new JTextField();
        txtLugarExpedicion = new JTextField("20000"); // CP de la agencia
        txtNumeroFactura = new JTextField("F-" + venta.getIdVenta()); // Folio sugerido

        cbUsoCFDI = new JComboBox<>(usosCFDI);
        cbMetodoPagoSAT = new JComboBox<>(metodosPagoSAT);
        cbFormaPagoSAT = new JComboBox<>(formasPagoSAT);

        panel.add(new JLabel("Folio Factura:"));
        panel.add(txtNumeroFactura);
        panel.add(new JLabel("RFC Cliente:"));
        panel.add(txtRFC);
        panel.add(new JLabel("Uso CFDI:"));
        panel.add(cbUsoCFDI);
        panel.add(new JLabel("Forma de Pago (SAT):"));
        panel.add(cbFormaPagoSAT);
        panel.add(new JLabel("Método de Pago (SAT):"));
        panel.add(cbMetodoPagoSAT);
        panel.add(new JLabel("Lugar Expedición (CP):"));
        panel.add(txtLugarExpedicion);
        panel.add(new JLabel("---"));
        panel.add(new JLabel("---"));
        panel.add(new JLabel("Subtotal:"));
        panel.add(txtSubtotal);
        panel.add(new JLabel("IVA (16%):"));
        panel.add(txtIVA);
        panel.add(new JLabel("Total:"));
        panel.add(txtTotal);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar Factura");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    /**
     * Llena el formulario con datos de la Venta y el Cliente.
     */
    private void precargarDatos() {
        // Pre-llenar RFC del cliente
        if (cliente != null && cliente.getRfc() != null) {
            txtRFC.setText(cliente.getRfc());
        }

        // Calcular y mostrar montos
        BigDecimal total = BigDecimal.valueOf(venta.getPrecioFinal());
        BigDecimal divisorIVA = BigDecimal.ONE.add(BigDecimal.valueOf(0.16));
        BigDecimal subtotal = total.divide(divisorIVA, 2, RoundingMode.HALF_UP);
        BigDecimal iva = total.subtract(subtotal);

        txtTotal.setText(String.format("%.2f", total.doubleValue()));
        txtSubtotal.setText(String.format("%.2f", subtotal.doubleValue()));
        txtIVA.setText(String.format("%.2f", iva.doubleValue()));
    }

    /**
     * Recolecta datos, crea el objeto Factura y lo guarda.
     */
    private void guardar() {
        if (txtRFC.getText().trim().length() < 10) {
            JOptionPane.showMessageDialog(this, "El RFC no es válido.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Crear el objeto Factura
        Factura f = new Factura();
        f.setIdVenta(venta.getIdVenta());
        f.setNumeroFactura(txtNumeroFactura.getText());
        f.setFechaFactura(new java.sql.Date(System.currentTimeMillis()));
        f.setLugarExpedicion(txtLugarExpedicion.getText());

        // Montos
        f.setSubtotal(Double.parseDouble(txtSubtotal.getText()));
        f.setIva(Double.parseDouble(txtIVA.getText()));
        f.setTotal(Double.parseDouble(txtTotal.getText()));

        // Catálogos del SAT (guardamos solo el código, ej: "G01")
        f.setUsoCfdi(((String) cbUsoCFDI.getSelectedItem()).split("-")[0]);
        f.setFormaPagoSat(((String) cbFormaPagoSAT.getSelectedItem()).split("-")[0]);
        f.setMetodoPagoSat(((String) cbMetodoPagoSAT.getSelectedItem()).split("-")[0]);
        f.setTipoComprobante("I"); // Ingreso

        // 2. Guardar usando el controlador
        if (controlador.registrarFactura(f)) {
            guardado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la factura en la BD.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}
