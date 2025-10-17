package services;

import dao.*;
import models.*;

import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Servicio principal para gestionar los tickets del sistema de parqueadero.
 * Aplica la tarifa activa, tiempo de gracia y registra el pago.
 */
public class TicketService {

    private final OperadorDAO operadorDAO;
    private final TicketDAO ticketDAO;
    private final MensualidadDAO mensualidadDAO;
    private final TarifaDAO tarifaDAO;
    private final PagoDAO pagoDAO;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
        this.mensualidadDAO = new MensualidadDAO();
        this.tarifaDAO = new TarifaDAO();
        this.pagoDAO = new PagoDAO();
        this.operadorDAO = new OperadorDAO();
    }

    /**
     * Registra el ingreso de un vehículo.
     */
    public Ticket registrarIngreso(String placa, int operadorIngresoId) throws SQLException {
        Ticket ticketExistente = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticketExistente != null) {
            throw new SQLException("Ya existe un ticket abierto para esta placa.");
        }

        Mensualidad mensualidad = mensualidadDAO.findVigenteByPlaca(placa);
        String tipoIngreso = (mensualidad != null) ? "MENSUALIDAD" : "INVITADO";

        String numeroFolio = ticketDAO.generarNumeroFolio();

        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setNumeroFolio(numeroFolio);
        nuevoTicket.setPlaca(placa);
        nuevoTicket.setTipoIngreso(tipoIngreso);
        nuevoTicket.setFechaIngreso(LocalDateTime.now());
        nuevoTicket.setOperadorIngresoId(operadorIngresoId);
        nuevoTicket.setActivo(true);
        nuevoTicket.setPagado(tipoIngreso.equals("MENSUALIDAD"));
        nuevoTicket.setMontoCobrado(BigDecimal.ZERO);
        nuevoTicket.setQrCode("QR-" + numeroFolio + "-" + placa.toUpperCase());


        Operador nombreOperador = operadorDAO.findById(operadorIngresoId);
        Ticket nuevoTicke = ticketDAO.create(nuevoTicket);
        try {
            TicketPrinterService printer = new TicketPrinterService(nuevoTicket, nombreOperador.getNombre());
            printer.imprimir();
        } catch (PrinterException e) {
            System.err.println("Error al imprimir ticket: " + e.getMessage());
        }

        return  nuevoTicke;
    }

    /**
     * Registra la salida del vehículo aplicando las reglas de negocio:
     * - Calcula tiempo total de estadía
     * - Aplica tiempo de gracia
     * - Calcula monto según tarifa activa
     * - Si es mensualidad → salida automática sin cobro
     * - Si requiere cobro → registra el pago
     */
    public boolean registrarSalida(String placa, int operadorSalidaId, String metodoPago) throws SQLException {
        Ticket ticket = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticket == null) {
            throw new SQLException("No se encontró un ticket abierto para esta placa.");
        }

        //Si tiene mensualidad, salida automática sin cobro
        if (ticket.getTipoIngreso().equals("MENSUALIDAD")) {
            return ticketDAO.registrarSalida(ticket.getId(), operadorSalidaId, 0);
        }

        // Obtener la tarifa activa
        Tarifa tarifa = tarifaDAO.findTarifaActiva();
        if (tarifa == null) {
            throw new SQLException("No hay tarifa activa configurada en el sistema.");
        }

        //Calcular tiempo de estadía
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = Duration.between(ticket.getFechaIngreso(), ahora).toMinutes();

        // Aplicar tiempo de gracia
        int tiempoGracia = tarifa.getTiempoGraciaMinutos();
        BigDecimal monto = BigDecimal.ZERO;

        if (minutos > tiempoGracia) {
            long minutosCobrables = minutos - tiempoGracia;

            double horas = minutosCobrables / 60.0;
            long horasCompletas = (long) Math.floor(horas);
            long minutosRestantes = minutosCobrables - (horasCompletas * 60);

            // Cobro por horas completas
            monto = tarifa.getValorBaseHora().multiply(BigDecimal.valueOf(horasCompletas));

            // Cobro por fracción adicional
            if (minutosRestantes > 0) {
                monto = monto.add(tarifa.getValorFraccion());
            }

            // Aplicar tope diario si corresponde
            if (tarifa.getTopeDiario() != null && monto.compareTo(tarifa.getTopeDiario()) > 0) {
                monto = tarifa.getTopeDiario();
            }
        }

        //Registrar salida
        boolean salidaRegistrada = ticketDAO.registrarSalida(ticket.getId(), operadorSalidaId, (int) minutos);

        //Si hay cobro, registrar el pago
        if (salidaRegistrada) {
            if (monto.compareTo(BigDecimal.ZERO) > 0) {
                Pago pago = new Pago();
                pago.setTicketId(ticket.getId());
                pago.setMonto(monto);
                pago.setMetodoPago(metodoPago);
                pago.setOperadorId(operadorSalidaId);
                pago.setFechaPago(LocalDateTime.now());
                pagoDAO.create(pago);
            }

            // Actualizar ticket como pagado
            ticketDAO.registrarPago(ticket.getId(), monto);
        }

        return salidaRegistrada;
    }

    /**
     * Registra un pago manual (usado por operador desde el panel).
     */
    public boolean registrarPagoManual(String placa, BigDecimal monto, String metodoPago, int operadorId) throws SQLException {
        Ticket ticket = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticket == null) {
            throw new SQLException("No se encontró un ticket abierto para esta placa.");
        }

        if (ticket.getTipoIngreso().equals("MENSUALIDAD")) {
            throw new SQLException("El vehículo con mensualidad no requiere pago.");
        }

        // Registrar pago manual
        Pago pago = new Pago();
        pago.setTicketId(ticket.getId());
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setOperadorId(operadorId);
        pago.setFechaPago(LocalDateTime.now());
        pagoDAO.create(pago);

        // Marcar ticket como pagado
        return ticketDAO.registrarPago(ticket.getId(), monto);
    }
}

