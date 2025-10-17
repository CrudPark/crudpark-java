package controllers;

import exceptios.BusinessException; // Asegúrate de que este import sea correcto (exceptions.BusinessException si es el paquete correcto)
import models.Ticket;
import services.TicketService;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Controlador encargado de gestionar las acciones relacionadas con los tickets.
 *
 */
public class TicketController {

    private final TicketService ticketService;

    public TicketController() {
        this.ticketService = new TicketService();
    }

    /**
     * Registra el ingreso de un vehículo y genera su ticket.
     *
     * @param placa Placa del vehículo
     * @param operadorIngresoId ID del operador que realiza el registro
     * @return El ticket generado o lanza BusinessException si falla
     */
    public Ticket registrarIngreso(String placa, int operadorIngresoId) {
        try {
            return ticketService.registrarIngreso(placa, operadorIngresoId);
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar ingreso: " + e.getMessage());
            throw new BusinessException(e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error inesperado al generar ticket: " + e.getMessage());
            throw new BusinessException("Error inesperado al registrar el ingreso.", e);
        }
    }

    /**
     * Registra la salida de un vehículo aplicando las tarifas y pagos correspondientes.
     *
     * @param placa Placa del vehículo
     * @param operadorSalidaId ID del operador que registra la salida
     * @param metodoPago Método de pago seleccionado
     * @return El monto cobrado (BigDecimal.ZERO si es mensualidad o tiempo de gracia).
     */
    public BigDecimal registrarSalida(String placa, int operadorSalidaId, String metodoPago) {
        try {
            return ticketService.registrarSalida(placa, operadorSalidaId, metodoPago);
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar salida: " + e.getMessage());
            throw new BusinessException(e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error inesperado al registrar salida: " + e.getMessage());
            throw new BusinessException("Error inesperado al registrar la salida.", e);
        }
    }

    /**
     * Registra un pago manual desde el panel del operador.
     *
     * @param placa Placa del vehículo
     * @param monto Monto del pago
     * @param metodoPago Método de pago (efectivo, tarjeta, etc.)
     * @param operadorId ID del operador que registra el pago
     * @return true si el pago fue exitoso, o lanza BusinessException si falló
     */
    public boolean registrarPagoManual(String placa, BigDecimal monto, String metodoPago, int operadorId) {
        try {
            return ticketService.registrarPagoManual(placa, monto, metodoPago, operadorId);
        } catch (SQLException e) {
            System.err.println("Error SQL al registrar pago manual: " + e.getMessage());
            throw new BusinessException(e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error inesperado al registrar pago manual: " + e.getMessage());
            throw new BusinessException("Error inesperado al registrar el pago manual.", e);
        }
    }
}