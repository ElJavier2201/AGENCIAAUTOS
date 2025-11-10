package vista;

import controlador.MarcaControlador;
import controlador.ModeloControlador;
import controlador.VehiculoControlador;
import modelo.Marca;
import modelo.Modelo;
import modelo.Vehiculo;
import util.ValidadorSwing;
import util.Validador;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

/**
 * JDialog para agregar o editar un Vehículo.
 *  ACTUALIZADO: Con validaciones de VIN y precios
 */
public class VehiculoForma extends JDialog {

    private final VehiculoControlador vehiculoControlador;
    private final MarcaControlador marcaControlador;
    private final ModeloControlador modeloControlador;
    private final Vehiculo vehiculo;
    private boolean guardado = false;

    // Componentes con validación
    private JComboBox<Marca> cbMarca;
    private JComboBox<Modelo> cbModelo;
    private JSpinner spinnerAnio;
    private JTextField txtColor;
    private JTextField txtKilometraje;
    private ValidadorSwing txtPrecio;
    private ValidadorSwing txtNumeroSerie; // VIN
    private JComboBox<String> cbEstado;
    private JLabel lblRutaImagen;

    private String nombreArchivoImagen = null;

    public VehiculoForma(Frame owner, Vehiculo vehiculo) {
        super(owner, true);
        this.vehiculo = vehiculo;
        this.vehiculoControlador = new VehiculoControlador();
        this.marcaControlador = new MarcaControlador();
        this.modeloControlador = new ModeloControlador();

        setTitle(vehiculo == null ? "Agregar Nuevo Vehículo" : "Editar Vehículo");
        setSize(550, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        cargarDatosIniciales();

        if (vehiculo != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.insets = new Insets(5, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ===== MARCA =====
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        panel.add(new JLabel("Marca: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        cbMarca = new JComboBox<>();
        panel.add(cbMarca, gbc);

        // ===== MODELO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Modelo: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        cbModelo = new JComboBox<>();
        panel.add(cbModelo, gbc);

        // ===== VIN (NÚMERO DE SERIE) =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("VIN (Número de Serie): *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNumeroSerie = new ValidadorSwing(17);
        txtNumeroSerie.setValidador(Validador::validarVin);
        txtNumeroSerie.setToolTipText("17 caracteres alfanuméricos (sin I, O, Q)");
        panel.add(txtNumeroSerie, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        // Labels de error
        JLabel lblErrorVin = new JLabel();
        lblErrorVin.setFont(new Font("Arial", Font.ITALIC, 10));
        txtNumeroSerie.setLabelError(lblErrorVin);
        panel.add(lblErrorVin, gbc);

        // ===== AÑO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Año: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        spinnerAnio = new JSpinner(new SpinnerNumberModel(2024, 1990, 2030, 1));
        panel.add(spinnerAnio, gbc);

        // ===== COLOR =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Color: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtColor = new JTextField();
        panel.add(txtColor, gbc);

        // ===== KILOMETRAJE =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Kilometraje:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtKilometraje = new JTextField("0");
        panel.add(txtKilometraje, gbc);

        // ===== PRECIO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Precio: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPrecio = new ValidadorSwing(15);
        txtPrecio.setValidador(texto -> {
            if (texto == null || texto.trim().isEmpty()) {
                return "El precio es obligatorio";
            }
            try {
                double precio = Double.parseDouble(texto);
                return Validador.validarPositivo(precio, "El precio");
            } catch (NumberFormatException e) {
                return "El precio debe ser un número válido";
            }
        });
        txtPrecio.setToolTipText("Precio en pesos. Ej: 250000.00");
        panel.add(txtPrecio, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        JLabel lblErrorPrecio = new JLabel();
        lblErrorPrecio.setFont(new Font("Arial", Font.ITALIC, 10));
        txtPrecio.setLabelError(lblErrorPrecio);
        panel.add(lblErrorPrecio, gbc);

        // ===== ESTADO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Estado: *"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        cbEstado = new JComboBox<>(new String[]{"nuevo", "usado", "reservado"});
        panel.add(cbEstado, gbc);

        // ===== IMAGEN =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Imagen:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JButton btnElegirImagen = new JButton("Seleccionar Archivo...");
        panel.add(btnElegirImagen, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        lblRutaImagen = new JLabel("(Sin imagen seleccionada)");
        lblRutaImagen.setFont(new Font("Arial", Font.ITALIC, 10));
        panel.add(lblRutaImagen, gbc);

        // Nota
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 10));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

        // Listeners
        cbMarca.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Marca marcaSeleccionada = (Marca) cbMarca.getSelectedItem();
                if (marcaSeleccionada != null) {
                    cargarModelos(marcaSeleccionada.getIdMarca());
                }
            }
        });

        btnElegirImagen.addActionListener(e -> seleccionarImagen());

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Imágenes (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            try {
                File destinoDir = new File("src/recursos/imagenes_autos");
                if (!destinoDir.exists()) {
                    destinoDir.mkdirs();
                }

                File archivoDestino = new File(destinoDir.getAbsolutePath() +
                        File.separator + archivoSeleccionado.getName());

                Files.copy(archivoSeleccionado.toPath(), archivoDestino.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                nombreArchivoImagen = archivoSeleccionado.getName();
                lblRutaImagen.setText(nombreArchivoImagen);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al copiar la imagen al directorio de recursos.",
                        "Error de Archivo",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarDatosIniciales() {
        List<Marca> marcas = marcaControlador.listarMarcas();
        for (Marca m : marcas) {
            cbMarca.addItem(m);
        }
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

        nombreArchivoImagen = vehiculo.getImagenPath();
        if (nombreArchivoImagen != null && !nombreArchivoImagen.isEmpty()) {
            lblRutaImagen.setText(nombreArchivoImagen);
        }

        // Precargar marca y modelo
        for (int i = 0; i < cbMarca.getItemCount(); i++) {
            Marca m = cbMarca.getItemAt(i);
            if (m.getIdMarca() == vehiculo.getIdModelo()) {
                cbMarca.setSelectedItem(m);
                break;
            }
        }
    }

    private void guardar() {
        // ✅ VALIDACIONES
        if (cbModelo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una marca y modelo.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean valido = true;
        valido &= txtNumeroSerie.validar();
        valido &= txtPrecio.validar();

        if (txtColor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El color es obligatorio.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!valido) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los errores marcados antes de guardar.",
                    "Errores de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Modelo modeloSeleccionado = (Modelo) cbModelo.getSelectedItem();
            String numeroSerie = Validador.normalizarVin(txtNumeroSerie.getTextoNormalizado());
            int anio = (int) spinnerAnio.getValue();
            String color = txtColor.getText().trim();
            int kilometraje = Integer.parseInt(txtKilometraje.getText().isEmpty() ?
                    "0" : txtKilometraje.getText());
            double precio = Double.parseDouble(txtPrecio.getTextoNormalizado());
            String estado = (String) cbEstado.getSelectedItem();

            boolean exito;

            if (vehiculo == null) {
                Vehiculo v = new Vehiculo();
                v.setIdModelo(modeloSeleccionado.getIdModelo());
                v.setNumeroSerie(numeroSerie);
                v.setAnio(anio);
                v.setColor(color);
                v.setKilometraje(kilometraje);
                v.setPrecio(precio);
                v.setEstado(estado);
                v.setImagenPath(nombreArchivoImagen);

                exito = vehiculoControlador.agregarVehiculo(v);
            } else {
                vehiculo.setIdModelo(modeloSeleccionado.getIdModelo());
                vehiculo.setNumeroSerie(numeroSerie);
                vehiculo.setAnio(anio);
                vehiculo.setColor(color);
                vehiculo.setKilometraje(kilometraje);
                vehiculo.setPrecio(precio);
                vehiculo.setEstado(estado);
                vehiculo.setImagenPath(nombreArchivoImagen);

                exito = vehiculoControlador.actualizarVehiculo(vehiculo);
            }

            if (exito) {
                guardado = true;
                JOptionPane.showMessageDialog(this,
                        "Vehículo guardado exitosamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el vehículo. Verifique que el VIN no esté duplicado.",
                        "Error de Base de Datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en formato numérico. Verifique kilometraje y precio.",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}