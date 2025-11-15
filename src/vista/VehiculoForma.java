package vista;

// --- Imports que ya tenías ---
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.io.File;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * JDialog para agregar o editar un Vehículo.
 * ACTUALIZADO: Con validaciones, nuevo layout y panel de imagen profesional
 */
public class VehiculoForma extends JDialog {

    private final VehiculoControlador vehiculoControlador;
    private final MarcaControlador marcaControlador;
    private final ModeloControlador modeloControlador;
    private final Vehiculo vehiculo; // <-- Hecho variable de instancia
    private boolean guardado = false;
    private JComboBox<Marca> cbMarca;
    private JComboBox<Modelo> cbModelo;
    private JSpinner spinnerAnio;
    private JTextField txtColor;
    private JTextField txtKilometraje;
    private ValidadorSwing txtPrecio;
    private ValidadorSwing txtNumeroSerie; // VIN
    private JComboBox<String> cbEstado;

    private ImagePanel panelImagen;
    private String nombreArchivoImagen = null;
    private static final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private static final Color COLOR_FONDO_SECCION = new Color(245, 246, 247);
    private static final Color COLOR_TEXTO_ETIQUETA = new Color(44, 62, 80);
    private static final Color COLOR_BORDE = new Color(189, 195, 199);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(127, 140, 141);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

    public VehiculoForma(Frame owner, Vehiculo vehiculo) {
        super(owner, true);
        this.vehiculo = vehiculo; // Asignar a la variable de instancia
        this.vehiculoControlador = new VehiculoControlador();
        this.marcaControlador = new MarcaControlador();
        this.modeloControlador = new ModeloControlador();

        setTitle(vehiculo == null ? "Agregar Nuevo Vehículo" : "Editar Vehículo");
        setSize(800, 600); // Tamaño ajustado para 2 columnas
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Panel Principal con 2 columnas (formulario y imagen) ---
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(panelContenido, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // ===== COLUMNA IZQUIERDA (FORMULARIO) =====
        gbc.gridx = 0;
        gbc.weightx = 0.6; // Ocupa el 60% del ancho
        gbc.weighty = 1.0;
        gbc.gridy = 0;
        panelContenido.add(crearPanelFormulario(), gbc);

        // ===== COLUMNA DERECHA (IMAGEN) =====
        gbc.gridx = 1;
        gbc.weightx = 0.4; // Ocupa el 40% del ancho
        gbc.weighty = 1.0;
        gbc.gridy = 0;
        panelContenido.add(crearPanelImagen(), gbc);


        // ===== PANEL DE BOTONES (Parte inferior del diálogo) =====
        add(crearPanelBotones(), BorderLayout.SOUTH);

        cargarDatosIniciales();

        if (vehiculo != null) {
            precargarDatos();
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBackground(COLOR_FONDO_SECCION);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_PRIMARIO.darker(), 1),
                        "Datos del Vehículo",
                        0, 0, FUENTE_ETIQUETA, COLOR_PRIMARIO.darker()
                ),
                new EmptyBorder(10, 15, 10, 15)
        ));

        gbc.insets = new Insets(5, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ===== MARCA =====
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        panel.add(new JLabel("Marca: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbMarca = new JComboBox<>();
        cbMarca.setFont(FUENTE_CAMPO);
        panel.add(cbMarca, gbc);

        // ===== MODELO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Modelo: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbModelo = new JComboBox<>();
        cbModelo.setFont(FUENTE_CAMPO);
        panel.add(cbModelo, gbc);

        // ===== VIN (NÚMERO DE SERIE) =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("VIN (Número de Serie): *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNumeroSerie = new ValidadorSwing(17);
        txtNumeroSerie.setValidador(Validador::validarVin);
        txtNumeroSerie.setToolTipText("17 caracteres alfanuméricos (sin I, O, Q)");
        txtNumeroSerie.setFont(FUENTE_CAMPO);
        panel.add(txtNumeroSerie, gbc);

        gbc.gridx = 1; gbc.gridy = ++row;
        JLabel lblErrorVin = new JLabel();
        lblErrorVin.setFont(new Font("Arial", Font.ITALIC, 10));
        txtNumeroSerie.setLabelError(lblErrorVin);
        panel.add(lblErrorVin, gbc);

        // ===== AÑO =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Año: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spinnerAnio = new JSpinner(new SpinnerNumberModel(2024, 1990, 2030, 1));
        spinnerAnio.setFont(FUENTE_CAMPO);
        panel.add(spinnerAnio, gbc);

        // ===== COLOR =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Color: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtColor = new JTextField();
        txtColor.setFont(FUENTE_CAMPO);
        panel.add(txtColor, gbc);

        // ===== KILOMETRAJE =====
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0.0;
        panel.add(new JLabel("Kilometraje:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtKilometraje = new JTextField("0");
        txtKilometraje.setFont(FUENTE_CAMPO);
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
        txtPrecio.setFont(FUENTE_CAMPO);
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
        cbEstado.setFont(FUENTE_CAMPO);
        panel.add(cbEstado, gbc);

        // Nota
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.weighty = 1.0; // Empujar al fondo
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 10));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);

        // Aplicar estilos a las etiquetas
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel label) {
                label.setFont(FUENTE_ETIQUETA);
                label.setForeground(COLOR_TEXTO_ETIQUETA);
            }
        }

        // Listeners
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

    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO_SECCION);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_PRIMARIO.darker(), 1),
                        "Imagen del Vehículo",
                        0, 0, FUENTE_ETIQUETA, COLOR_PRIMARIO.darker()
                ),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // --- NUESTRO PANEL DE IMAGEN MEJORADO ---
        panelImagen = new ImagePanel();
        panelImagen.setBackground(new Color(230, 230, 230)); // Fondo gris claro
        panelImagen.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        panel.add(panelImagen, BorderLayout.CENTER);

        // Botón de seleccionar imagen
        JButton btnElegirImagen = crearBoton("Seleccionar Archivo...", COLOR_PRIMARIO);
        btnElegirImagen.addActionListener(e -> seleccionarImagen());

        panel.add(btnElegirImagen, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(COLOR_FONDO_SECCION);
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        JButton btnGuardar = crearBoton("Guardar", COLOR_PRIMARIO);
        btnGuardar.addActionListener(e -> guardar());

        JButton btnCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
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
                // Definir el directorio de destino
                Path destinoDir = Path.of("src", "recursos", "imagenes_autos");
                if (!Files.exists(destinoDir)) {
                    Files.createDirectories(destinoDir);
                }

                // Crear un nombre de archivo único (o usar el original)
                String nombreArchivo = archivoSeleccionado.getName();
                Path archivoDestino = destinoDir.resolve(nombreArchivo);

                // Copiar el archivo
                Files.copy(archivoSeleccionado.toPath(), archivoDestino,
                        StandardCopyOption.REPLACE_EXISTING);

                // Guardar solo el nombre del archivo
                nombreArchivoImagen = nombreArchivo;
                panelImagen.setImage(archivoDestino.toUri().toString()); // Mostrar la imagen copiada

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
        cbMarca.addItem(null); // Opción nula
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
        if (vehiculo == null) return;

        txtNumeroSerie.setText(vehiculo.getNumeroSerie());
        spinnerAnio.setValue(vehiculo.getAnio());
        txtColor.setText(vehiculo.getColor());
        txtKilometraje.setText(String.valueOf(vehiculo.getKilometraje()));
        txtPrecio.setText(String.format("%.2f", vehiculo.getPrecio()));
        cbEstado.setSelectedItem(vehiculo.getEstado());

        nombreArchivoImagen = vehiculo.getImagenPath();
        if (nombreArchivoImagen != null && !nombreArchivoImagen.isEmpty()) {
            panelImagen.setImage(nombreArchivoImagen);
        }

        // Precargar marca y modelo
        // (Esta parte es compleja y depende de cómo estén cargados tus modelos)
        // (Dejamos la lógica simplificada por ahora)
        if (vehiculo.getIdModelo() > 0) {
            // Idealmente, aquí deberías buscar la Marca del Modelo
            // y luego cargar los modelos de esa marca
            // ...
        }
    }

    private void guardar() {
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

            Vehiculo v = (vehiculo == null) ? new Vehiculo() : this.vehiculo;

            v.setIdModelo(modeloSeleccionado.getIdModelo());
            v.setNumeroSerie(numeroSerie);
            v.setAnio(anio);
            v.setColor(color);
            v.setKilometraje(kilometraje);
            v.setPrecio(precio);
            v.setEstado(estado);
            v.setImagenPath(nombreArchivoImagen); // Asignar el nombre del archivo

            if (vehiculo == null) {
                exito = vehiculoControlador.agregarVehiculo(v);
            } else {
                exito = vehiculoControlador.actualizarVehiculo(v);
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

    /**
     * Clase interna para mostrar la imagen del vehículo de forma profesional.
     * Carga asíncronamente y escala la imagen para ajustarse al panel.
     */
    private class ImagePanel extends JPanel {
        private Image image;
        private String currentImagePath; // Guarda la ruta para evitar recargas innecesarias
        private Image PLACEHOLDER_IMAGE; // Imagen de "No disponible"
        private final JLabel lblPlaceholder;

        public ImagePanel() {
            super();
            setOpaque(true); // El fondo gris claro debe ser visible
            setLayout(new GridBagLayout()); // Usar GridBag para centrar el texto

            // Cargar imagen de placeholder
            try {
                URL placeholderUrl = getClass().getResource("/recursos/imagen_nodisponible.jpg");
                if (placeholderUrl != null) {
                    PLACEHOLDER_IMAGE = new ImageIcon(placeholderUrl).getImage();
                }
            } catch (Exception e) {
                PLACEHOLDER_IMAGE = null;
                System.err.println("No se pudo cargar la imagen placeholder");
            }

            // Texto de ayuda si no hay imagen
            lblPlaceholder = new JLabel("Click para seleccionar imagen", SwingConstants.CENTER);
            lblPlaceholder.setForeground(COLOR_TEXTO_SECUNDARIO);
            lblPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            add(lblPlaceholder); // Añadir el placeholder

            // Listener para seleccionar imagen al hacer clic en el panel
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionarImagen();
                }
            });
        }

        public void setImage(String imagePath) {
            if (Objects.equals(this.currentImagePath, imagePath)) {
                return; // No recargar si es la misma imagen
            }
            this.currentImagePath = imagePath;
            this.image = null; // Limpiar la imagen actual
            lblPlaceholder.setVisible(false); // Ocultar texto
            repaint();

            // Cargar la imagen en un hilo secundario
            new SwingWorker<Image, Void>() {
                @Override
                protected Image doInBackground() {
                    try {
                        URL imageUrl;
                        if (imagePath.startsWith("file:") || imagePath.startsWith("http:") || imagePath.startsWith("https://")) {
                            imageUrl = new URL(imagePath);
                        } else if (imagePath.startsWith(File.separator) || imagePath.matches("^[A-Za-z]:\\\\.*")) {
                            // Es una ruta absoluta del sistema de archivos
                            imageUrl = new File(imagePath).toURI().toURL();
                        } else {
                            // Es una ruta de recurso (la guardada en la BD)
                            imageUrl = getClass().getResource("/recursos/imagenes_autos/" + imagePath);
                        }

                        if (imageUrl != null) {
                            return new ImageIcon(imageUrl).getImage();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al cargar la imagen: " + imagePath + " - " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        Image loadedImage = get();
                        if (loadedImage != null) {
                            ImagePanel.this.image = loadedImage;
                        } else {
                            ImagePanel.this.image = PLACEHOLDER_IMAGE; // Usa el placeholder
                            lblPlaceholder.setText("Imagen no encontrada");
                            lblPlaceholder.setVisible(true);
                        }
                        repaint(); // Volver a pintar el panel con la nueva imagen
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error en SwingWorker al obtener imagen: " + e.getMessage());
                        ImagePanel.this.image = PLACEHOLDER_IMAGE; // En caso de error en el worker
                        lblPlaceholder.setText("Error al cargar");
                        lblPlaceholder.setVisible(true);
                        repaint();
                    }
                }
            }.execute();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Pinta el fondo gris claro
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (image != null) {
                // Calcular las dimensiones para que la imagen se ajuste y mantenga el aspecto
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                int imgWidth = image.getWidth(this);
                int imgHeight = image.getHeight(this);

                if (imgWidth <= 0 || imgHeight <= 0) {
                    g2.dispose();
                    return;
                }

                double scaleX = (double) panelWidth / imgWidth;
                double scaleY = (double) panelHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY); // Usar el menor para que quepa (letterbox)

                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);

                int x = (panelWidth - newWidth) / 2; // Centrar horizontalmente
                int y = (panelHeight - newHeight) / 2; // Centrar verticalmente
                int CORNER_RADIUS = 15;
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.setClip(roundedRect);

                g2.drawImage(image, x, y, newWidth, newHeight, this);
            }
            g2.dispose();
        }
    }
}