
import vista.LoginPanel;
import vista.PortalCliente;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la aplicación Agencia de Autos.
 */
public class Main {
    public static void main(String[] args) {

        // Asegura que la UI se ejecute en el hilo de Swing
        SwingUtilities.invokeLater(() -> {

            // 1. Preguntar al usuario su rol
            Object[] opciones = {"Soy Empleado (Iniciar Sesión)", "Soy Cliente (Ver Catálogo)"};
            int seleccion = JOptionPane.showOptionDialog(
                    null, // Componente padre
                    "Bienvenido a la Agencia de Autos", // Mensaje
                    "Seleccionar Acceso", // Título
                    JOptionPane.YES_NO_OPTION, // Tipo de opción
                    JOptionPane.QUESTION_MESSAGE, // Tipo de mensaje
                    null, // Icono (default)
                    opciones, // Texto de los botones
                    opciones[0] // Botón por defecto
            );

            // 2. Abrir la ventana correspondiente
            if (seleccion == 0) {
                // 0 = Empleado
                new LoginPanel().setVisible(true);
            } else if (seleccion == 1) {
                // 1 = Cliente
                new PortalCliente().setVisible(true);
            }
            // Si el usuario cierra el diálogo (seleccion == -1), la aplicación simplemente termina.

        });
    }
}