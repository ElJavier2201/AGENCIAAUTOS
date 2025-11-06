package vista;

import controlador.MarcaControlador;
import controlador.ModeloControlador;
import controlador.VehiculoControlador;
import modelo.Marca;
import modelo.Modelo;
import modelo.Vehiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Objects;

/**
 * JDialog (ventana emergente) para agregar o editar un Vehículo.
 */
public class VehiculoForma extends JDialog {

    private final VehiculoControlador vehiculoControlador;
    private final MarcaControlador marcaControlador;
    private final ModeloControlador modeloControlador;

    private final Vehiculo vehiculo; // null si es 'nuevo', o el objeto si es 'editar'
    private boolean guardado = false; // Para saber si el usuario presionó "Guardar"

    // Componentes del Formulario
    private JComboBox<Marca> cbMarca;
    private JComboBox<Modelo> cbModelo;
    private JSpinner spinnerAnio;
    private JTextField txtColor;
    private JTextField txtKilometraje;
    private JTextField txtPrecio;
    private JTextField txtNumeroSerie;
    private JComboBox<String> cbEstado;

    public VehiculoForma(Frame owner, Vehiculo vehiculo) {
        super(owner, true); // Modal
        this.vehiculo = vehiculo;
        this.vehiculoControlador = new VehiculoControlador();
        this.marcaControlador = new MarcaControlador();
        this.modeloControlador = new ModeloControlador();

        setTitle(vehiculo == null ? "Agregar Nuevo Vehículo" : "Editar Vehículo");
        setSize(450, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        cargarDatosIniciales();

        // Si estamos editando, llenar el formulario con los datos del vehículo
        if (vehiculo != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos
        cbMarca = new JComboBox<>();
        cbModelo = new JComboBox<>();
        spinnerAnio = new JSpinner(new SpinnerNumberModel(2024, 2000, 2030, 1));
        txtColor = new JTextField();
        txtKilometraje = new JTextField();
        txtPrecio = new JTextField();
        txtNumeroSerie = new JTextField();
        cbEstado = new JComboBox<>(new String[]{"nuevo", "usado", "reservado"});

        // Labels
        panel.add(new JLabel("Marca:"));
        panel.add(cbMarca);
        panel.add(new JLabel("Modelo:"));
        panel.add(cbModelo);
        panel.add(new JLabel("Número de Serie (VIN):"));
        panel.add(txtNumeroSerie);
        panel.add(new JLabel("Año:"));
        panel.add(spinnerAnio);
        panel.add(new JLabel("Color:"));
        panel.add(txtColor);
        panel.add(new JLabel("Kilometraje:"));
        panel.add(txtKilometraje);
        panel.add(new JLabel("Precio:"));
        panel.add(txtPrecio);
        panel.add(new JLabel("Estado:"));
        panel.add(cbEstado);

        // --- Lógica de ComboBox Dinámico ---
        cbMarca.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Marca marcaSeleccionada = (Marca) cbMarca.getSelectedItem();
                if (marcaSeleccionada != null) {
                    cargarModelos(marcaSeleccionada.getIdMarca());
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private void cargarDatosIniciales() {
        // Cargar Marcas
        List<Marca> marcas = marcaControlador.listarMarcas();
        for (Marca m : marcas) {
            cbMarca.addItem(m);
        }
        // (El listener de cbMarca cargará los modelos del primer ítem)
    }

    private void cargarModelos(int idMarca) {
        cbModelo.removeAllItems();
        List<Modelo> modelos = modeloControlador.listarModelosPorMarca(idMarca);
        for (Modelo m : modelos) {
            cbModelo.addItem(m);
        }
    }

    private void precargarDatos() {
        txtNumeroSerie.setText(vehiculo.getNumeroSerie());
        spinnerAnio.setValue(vehiculo.getAnio());
        txtColor.setText(vehiculo.getColor());
        txtKilometraje.setText(String.valueOf(vehiculo.getKilometraje()));
        txtPrecio.setText(String.format("%.2f", vehiculo.getPrecio()));
        cbEstado.setSelectedItem(vehiculo.getEstado());

        // Seleccionar la Marca (esto es un poco más complejo)
        for (int i = 0; i < cbMarca.getItemCount(); i++) {
            if (cbMarca.getItemAt(i).getNombreMarca().equals(vehiculo.getNombreMarca())) {
                cbMarca.setSelectedIndex(i);
                break;
            }
        }

        // Seleccionar el Modelo (debe hacerse después de que se carguen los modelos)
        // (El listener de cbMarca cargará los modelos correctos)
        Timer timer = new Timer(50, e -> { // Pequeño retraso para dejar que cbModelo se cargue
            for (int i = 0; i < cbModelo.getItemCount(); i++) {
                if (cbModelo.getItemAt(i).getIdModelo() == vehiculo.getIdModelo()) {
                    cbModelo.setSelectedIndex(i);
                    break;
                }
            }
            ((Timer)e.getSource()).stop(); // Detener el timer
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void guardar() {
        // --- Validación Simple (puedes mejorarla) ---
        if (txtNumeroSerie.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Número de Serie y Precio son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del formulario
        Modelo modeloSeleccionado = (Modelo) cbModelo.getSelectedItem();
        String numeroSerie = txtNumeroSerie.getText();
        int anio = (int) spinnerAnio.getValue();
        String color = txtColor.getText();
        int kilometraje = Integer.parseInt(txtKilometraje.getText().isEmpty() ? "0" : txtKilometraje.getText());
        double precio = Double.parseDouble(txtPrecio.getText());
        String estado = (String) cbEstado.getSelectedItem();

        boolean exito;

        if (vehiculo == null) {
            // --- MODO AGREGAR ---
            Vehiculo v = new Vehiculo();
            v.setIdModelo(modeloSeleccionado.getIdModelo());
            v.setNumeroSerie(numeroSerie);
            v.setAnio(anio);
            v.setColor(color);
            v.setKilometraje(kilometraje);
            v.setPrecio(precio);
            v.setEstado(estado);

            exito = vehiculoControlador.agregarVehiculo(v);
        } else {
            // --- MODO EDITAR ---
            vehiculo.setIdModelo(modeloSeleccionado.getIdModelo());
            vehiculo.setNumeroSerie(numeroSerie);
            vehiculo.setAnio(anio);
            vehiculo.setColor(color);
            vehiculo.setKilometraje(kilometraje);
            vehiculo.setPrecio(precio);
            vehiculo.setEstado(estado);

            exito = vehiculoControlador.actualizarVehiculo(vehiculo);
        }

        if (exito) {
            guardado = true;
            dispose(); // Cierra la ventana
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el vehículo.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Permite al panel principal saber si se guardaron los cambios.
     */
    public boolean isGuardado() {
        return guardado;
    }
}