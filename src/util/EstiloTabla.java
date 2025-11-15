

package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * NUEVA CLASE: Renderizador personalizado para dar estilo a todas las JTable.
 * Aplica bandas de color (row striping) y alineación de texto.
 */
public class EstiloTabla extends DefaultTableCellRenderer {

    // --- Define tus colores aquí ---
    private static final Color COLOR_FILA_PAR = Color.WHITE;
    private static final Color COLOR_FILA_IMPAR = new Color(245, 246, 247); // Un gris muy claro
    private static final Color COLOR_SELECCION = new Color(52, 152, 219); // Azul primario de tu app
    private static final Color COLOR_TEXTO = new Color(44, 62, 80); // Texto principal oscuro
    private static final Color COLOR_TEXTO_SELECCION = Color.WHITE;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        // Llama al método padre para que pinte el componente
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Configuración de fuente y color de texto
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // --- Lógica de Selección ---
        if (isSelected) {
            c.setBackground(COLOR_SELECCION);
            c.setForeground(COLOR_TEXTO_SELECCION);
        } else {
            // --- Lógica de Bandas (Row Striping) ---
            if (row % 2 == 0) {
                c.setBackground(COLOR_FILA_PAR);
            } else {
                c.setBackground(COLOR_FILA_IMPAR);
            }
            c.setForeground(COLOR_TEXTO);
        }

        // --- Lógica de Alineación ---
        if (value instanceof Number || (value != null && value.toString().startsWith("$"))) {
            // Alinear números y dinero a la derecha
            setHorizontalAlignment(SwingConstants.RIGHT);
            // Añadir un pequeño padding a la derecha
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        } else if (value instanceof String && ((String) value).matches("^\\d+$")) {
            // Centrar IDs (números enteros)
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        } else {
            // Alinear texto a la izquierda
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        return c;
    }
}
