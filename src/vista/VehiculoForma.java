package vista;

import controlador.MarcaControlador;
import controlador.ModeloControlador;
import controlador.VehiculoControlador;
import modelo.Marca;
import modelo.Modelo;
import modelo.Vehiculo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Objects;
// --- NUEVO: Import para Files (copiar archivo) ---
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

/**
 * JDialog para agregar o editar un Vehículo.
 * (Actualizado para guardar solo el nombre del archivo y copiarlo a recursos)
 */
public class VehiculoForma extends JDialog {

    private final VehiculoControlador vehiculoControlador;
    private final MarcaControlador marcaControlador;
    private final ModeloControlador modeloControlador;

    private final Vehiculo vehiculo;
    private boolean guardado = false;

    // Componentes del Formulario
    private JComboBox<Marca> cbMarca;
    private JComboBox<Modelo> cbModelo;
    private JSpinner spinnerAnio;
    private JTextField txtColor;
    private JTextField txtKilometraje;
    private JTextField txtPrecio;
    private JTextField txtNumeroSerie;
    private JComboBox<String> cbEstado;
    private JButton btnElegirImagen;
    private JLabel lblRutaImagen;

    // --- MODIFICADO: Ahora guarda solo el nombre del archivo ---
    private String nombreArchivoImagen = null;

    public VehiculoForma(Frame owner, Vehiculo vehiculo) {
        super(owner, true);
        this.vehiculo = vehiculo;
        this.vehiculoControlador = new VehiculoControlador();
        this.marcaControlador = new MarcaControlador();
        this.modeloControlador = new ModeloControlador();

        setTitle(vehiculo == null ? "Agregar Nuevo Vehículo" : "Editar Vehículo");
        setSize(500, 450);
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
        // ... (Este método no cambia, sigue igual que en la versión anterior)
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cbMarca = new JComboBox<>();
        cbModelo = new JComboBox<>();
        spinnerAnio = new JSpinner(new SpinnerNumberModel(2024, 2000, 2030, 1));
        txtColor = new JTextField();
        txtKilometraje = new JTextField();
        txtPrecio = new JTextField();
        txtNumeroSerie = new JTextField();
        cbEstado = new JComboBox<>(new String[]{"nuevo", "usado", "reservado"});
        btnElegirImagen = new JButton("Seleccionar Archivo...");
        lblRutaImagen = new JLabel("(Sin imagen seleccionada)");
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
        panel.add(new JLabel("Imagen del Vehículo:"));
        panel.add(btnElegirImagen);
        panel.add(new JLabel(""));
        panel.add(lblRutaImagen);
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
        // ... (Este método no cambia)
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    /**
     * --- MÉTODO MODIFICADO ---
     * 1. Abre el JFileChooser.
     * 2. Copia la imagen seleccionada a src/recursos/imagenes_autos.
     * 3. Guarda solo el NOMBRE del archivo.
     */
    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Imágenes (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            // --- Lógica de copiado ---
            try {
                // Definimos la carpeta de destino (relativa al proyecto)
                File destinoDir = new File("src/recursos/imagenes_autos");
                if (!destinoDir.exists()) {
                    destinoDir.mkdirs(); // Crear la carpeta si no existe
                }

                // Creamos el archivo de destino
                File archivoDestino = new File(destinoDir.getAbsolutePath() + File.separator + archivoSeleccionado.getName());

                // Copiamos el archivo
                Files.copy(archivoSeleccionado.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // --- MODIFICADO: Guardar solo el nombre del archivo ---
                nombreArchivoImagen = archivoSeleccionado.getName();
                lblRutaImagen.setText(nombreArchivoImagen);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al copiar la imagen al directorio de recursos.", "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ... (cargarDatosIniciales y cargarModelos no cambian)
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

    /**
     * --- MÉTODO MODIFICADO ---
     * Lee solo el nombre del archivo desde el objeto Vehiculo.
     */
    private void precargarDatos() {
        txtNumeroSerie.setText(vehiculo.getNumeroSerie());
        spinnerAnio.setValue(vehiculo.getAnio());
        // ... (resto de campos)
        txtColor.setText(vehiculo.getColor());
        txtKilometraje.setText(String.valueOf(vehiculo.getKilometraje()));
        txtPrecio.setText(String.format("%.2f", vehiculo.getPrecio()));
        cbEstado.setSelectedItem(vehiculo.getEstado());

        // --- MODIFICADO: Lee solo el nombre del archivo ---
        nombreArchivoImagen = vehiculo.getImagenPath();
        if (nombreArchivoImagen != null && !nombreArchivoImagen.isEmpty()) {
            lblRutaImagen.setText(nombreArchivoImagen);
        } else {
            lblRutaImagen.setText("(Sin imagen)");
        }

        // ... (Lógica de precargar Marca y Modelo - Sin cambios)
        for (int i = 0; i < cbMarca.getItemCount(); i++) { /* ... */ }
        Timer timer = new Timer(50, e -> { /* ... */ });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * --- MÉTODO MODIFICADO ---
     * Guarda solo el nombre del archivo en la BD.
     */
    private void guardar() {
        // ... (Validaciones sin cambios)
        if (txtNumeroSerie.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Número de Serie y Precio son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ... (Recolección de datos sin cambios)
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
            // ... (resto de v.set...)
            v.setIdModelo(modeloSeleccionado.getIdModelo());
            v.setNumeroSerie(numeroSerie);
            v.setAnio(anio);
            v.setColor(color);
            v.setKilometraje(kilometraje);
            v.setPrecio(precio);
            v.setEstado(estado);
            v.setImagenPath(nombreArchivoImagen); // --- MODIFICADO ---

            exito = vehiculoControlador.agregarVehiculo(v);
        } else {
            // --- MODO EDITAR ---
            // ... (resto de vehiculo.set...)
            vehiculo.setIdModelo(modeloSeleccionado.getIdModelo());
            vehiculo.setNumeroSerie(numeroSerie);
            vehiculo.setAnio(anio);
            vehiculo.setColor(color);
            vehiculo.setKilometraje(kilometraje);
            vehiculo.setPrecio(precio);
            vehiculo.setEstado(estado);
            vehiculo.setImagenPath(nombreArchivoImagen); // --- MODIFICADO ---

            exito = vehiculoControlador.actualizarVehiculo(vehiculo);
        }

        if (exito) {
            guardado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el vehículo.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}