package controlador;

import dao.PagoDAO;
import modelo.MetodoPago;
import modelo.Pago;
import modelo.Venta;

// --- NUEVO: Imports para calcular fechas ---
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
// --- FIN NUEVO ---

/**
 * Controlador para la lógica de Pagos.
 * (Actualizado para generar mensualidades pendientes)
 */
public class PagoControlador {

    private final PagoDAO dao;

    public PagoControlador() {
        this.dao = new PagoDAO();
    }

    /**
     * Registra el primer pago (enganche o pago total).
     */
    public boolean registrarPagoInicial(Venta venta, MetodoPago metodo) {
        if (venta == null || venta.getIdVenta() == 0) {
            return false;
        }

        Pago p = new Pago();
        p.setIdVenta(venta.getIdVenta());
        // p.setIdFactura(idFacturaGenerada); // (Opcional, si la factura ya se generó)

        p.setFechaPago(new java.sql.Date(System.currentTimeMillis()));
        p.setReferencia("VTA-" + venta.getIdVenta());
        p.setEstado("PAGADO");
        p.setFechaVencimiento(null);

        if (metodo.isRequiereFinanciamiento()) {
            p.setNumeroPago(0); // --- CAMBIO: '0' es el enganche (basado en tu BD) ---
            p.setMonto(venta.getEnganche());
            p.setConcepto("Enganche de vehículo");
        } else {
            p.setNumeroPago(1); // '1' es el pago único
            p.setMonto(venta.getPrecioFinal());
            p.setConcepto("Pago total de contado");
        }

        return dao.agregarPago(p);
    }

    /**
     * --- NUEVO MÉTODO ---
     * Calcula y genera todas las mensualidades pendientes para un financiamiento.
     */
    public boolean generarMensualidadesPendientes(Venta venta) {
        if (venta.getPlazoMeses() <= 0) {
            // No es un financiamiento o el plazo es 0
            return true;
        }

        try {
            // 1. Calcular el monto de cada mensualidad (con interés simple)
            // (Usamos BigDecimal para precisión monetaria)
            BigDecimal precioFinal = BigDecimal.valueOf(venta.getPrecioFinal());
            BigDecimal enganche = BigDecimal.valueOf(venta.getEnganche());
            BigDecimal tasaInteres = BigDecimal.valueOf(venta.getTasaInteres() / 100.0);
            int plazo = venta.getPlazoMeses();

            BigDecimal montoAFinanciar = precioFinal.subtract(enganche);
            BigDecimal interesTotal = montoAFinanciar.multiply(tasaInteres);
            BigDecimal totalAPagar = montoAFinanciar.add(interesTotal);

            // Monto de cada mensualidad
            BigDecimal montoMensual = totalAPagar.divide(BigDecimal.valueOf(plazo), 2, RoundingMode.HALF_UP);

            // 2. Preparar el DAO y el calendario
            PagoDAO pagoDAO = new PagoDAO();
            Calendar cal = Calendar.getInstance();
            cal.setTime(venta.getFechaVenta()); // Empezar desde la fecha de venta

            // 3. Generar N pagos
            for (int i = 1; i <= plazo; i++) {
                cal.add(Calendar.MONTH, 1); // Añadir 1 mes

                Pago p = new Pago();
                p.setIdVenta(venta.getIdVenta());
                p.setNumeroPago(i); // Mensualidad 1, 2, 3...
                p.setMonto(montoMensual.doubleValue());
                p.setConcepto("Mensualidad " + i + " de " + plazo);
                p.setEstado("PENDIENTE"); // Estado por defecto

                // Fecha de pago nula, Fecha de vencimiento es la calculada
                p.setFechaPago(null);
                p.setFechaVencimiento(new java.sql.Date(cal.getTimeInMillis()));

                // Guardar este pago en la BD
                if (!pagoDAO.agregarPago(p)) {
                    // Si un pago falla, detenemos la generación
                    System.err.println("Error al generar mensualidad " + i);
                    return false;
                }
            }
            return true; // Todos los pagos generados con éxito

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene la lista de todos los pagos de una venta específica.
     */
    public List<Pago> listarPagosPorVenta(int idVenta) {
        return dao.listarPagosPorVenta(idVenta);
    }

    /**
     * Registra el pago de una mensualidad pendiente.
     */
    public boolean pagarMensualidad(int idPago) {
        // Usamos la fecha actual para el pago
        java.sql.Date fechaDePago = new java.sql.Date(System.currentTimeMillis());
        return dao.pagarMensualidad(idPago, fechaDePago);
    }
}