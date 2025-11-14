package util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

/**
 *  NUEVO: Componente reutilizable de búsqueda para tablas
 * Incluye campo de texto, botón de búsqueda y filtros opcionales
 */
public class FiltrosPanel extends JPanel {

    private final JTextField txtBusqueda;
    private final JButton btnBuscar;
    private final JButton btnLimpiar;
    private final JComboBox<String> cbFiltro;
    private Consumer<String> onSearch;
    private Consumer<String> onFilterChange;
    private boolean busquedaTiempoReal = true;

    /**
     * Constructor básico (solo búsqueda)
     */
    public FiltrosPanel(String placeholder) {
        this(placeholder, null);
    }

    /**
     * Constructor con filtros
     * @param placeholder Texto del campo de búsqueda
     * @param filtros Opciones para el ComboBox de filtros (null si no se necesitan)
     */
    public FiltrosPanel(String placeholder, String[] filtros) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // ===== ÍCONO DE BÚSQUEDA =====
        JLabel lblIcono = new JLabel("🔍");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        add(lblIcono);

        // ===== CAMPO DE BÚSQUEDA =====
        txtBusqueda = new JTextField(25);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBusqueda.setToolTipText(placeholder);

        // Placeholder personalizado
        txtBusqueda.setText(placeholder);
        txtBusqueda.setForeground(Color.GRAY);

        txtBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtBusqueda.getText().equals(placeholder)) {
                    txtBusqueda.setText("");
                    txtBusqueda.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtBusqueda.getText().isEmpty()) {
                    txtBusqueda.setText(placeholder);
                    txtBusqueda.setForeground(Color.GRAY);
                }
            }
        });

        // Búsqueda en tiempo real mientras escribe
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { ejecutarBusqueda(); }
            public void removeUpdate(DocumentEvent e) { ejecutarBusqueda(); }
            public void insertUpdate(DocumentEvent e) { ejecutarBusqueda(); }

            private void ejecutarBusqueda() {
                if (busquedaTiempoReal && onSearch != null) {
                    String texto = getTextoBusqueda();
                    onSearch.accept(texto);
                }
            }
        });

        add(txtBusqueda);

        // ===== BOTÓN BUSCAR =====
        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(52, 152, 219));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBuscar.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBuscar.setBackground(new Color(52, 152, 219));
            }
        });

        btnBuscar.addActionListener(e -> {
            if (onSearch != null) {
                onSearch.accept(getTextoBusqueda());
            }
        });

        add(btnBuscar);

        // ===== BOTÓN LIMPIAR =====
        btnLimpiar = new JButton("✖");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setBackground(new Color(231, 76, 60));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setToolTipText("Limpiar búsqueda");

        btnLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLimpiar.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLimpiar.setBackground(new Color(231, 76, 60));
            }
        });

        btnLimpiar.addActionListener(e -> limpiarBusqueda());

        add(btnLimpiar);

        // ===== COMBOBOX DE FILTROS (OPCIONAL) =====
        if (filtros != null && filtros.length > 0) {
            add(new JLabel(" | Filtrar por:"));

            cbFiltro = new JComboBox<>(filtros);
            cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cbFiltro.setBackground(Color.WHITE);
            cbFiltro.setPreferredSize(new Dimension(150, 30));

            cbFiltro.addActionListener(e -> {
                if (onFilterChange != null) {
                    onFilterChange.accept((String) cbFiltro.getSelectedItem());
                }
            });

            add(cbFiltro);
        } else {
            cbFiltro = null;
        }
    }

    /**
     * Establece el callback que se ejecuta al buscar
     */
    public void setOnSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    /**
     * Establece el callback que se ejecuta al cambiar el filtro
     */
    public void setOnFilterChange(Consumer<String> onFilterChange) {
        this.onFilterChange = onFilterChange;
    }

    /**
     * Activa/desactiva la búsqueda en tiempo real
     */
    public void setBusquedaTiempoReal(boolean activa) {
        this.busquedaTiempoReal = activa;
        btnBuscar.setVisible(!activa); // Ocultar botón si es tiempo real
    }

    /**
     * Obtiene el texto actual de búsqueda (sin placeholder)
     */
    public String getTextoBusqueda() {
        String texto = txtBusqueda.getText();
        if (texto.equals(txtBusqueda.getToolTipText()) || texto.isEmpty()) {
            return "";
        }
        return texto.trim();
    }

    /**
     * Obtiene el filtro seleccionado
     */
    public String getFiltroSeleccionado() {
        if (cbFiltro == null) return null;
        return (String) cbFiltro.getSelectedItem();
    }

    /**
     * Limpia el campo de búsqueda
     */
    public void limpiarBusqueda() {
        txtBusqueda.setText("");
        txtBusqueda.setForeground(Color.BLACK);
        if (onSearch != null) {
            onSearch.accept("");
        }
    }

    /**
     * Enfoca el campo de búsqueda
     */
    public void enfocar() {
        txtBusqueda.requestFocusInWindow();
    }
}