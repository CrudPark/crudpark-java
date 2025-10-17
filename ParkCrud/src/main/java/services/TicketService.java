package services;

import config.DatabaseConfig;
import dao.*;
import models.*;

import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        Connection conn = null; // Inicializar la conexión fuera del try
        Ticket ticketCreado = null;

        // 1. Validaciones de lógica de negocio (No requieren conexión transaccional)
        if (placa == null || placa.trim().isEmpty()) {
            throw new SQLException("La placa no puede estar vacía");
        }

        placa = placa.trim().toUpperCase();

        // Las lecturas del DAO (findTicketAbiertoByPlaca, findVigenteByPlaca, generarNumeroFolio)
        // pueden usar el comportamiento por defecto de su DAO (autocommit=true), ya que son solo lecturas
        // y no forman parte de la unidad de trabajo crítica (la escritura).

        Ticket ticketExistente = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticketExistente != null) {
            throw new SQLException("Ya existe un ticket abierto para la placa: " + placa);
        }

        Mensualidad mensualidad = mensualidadDAO.findVigenteByPlaca(placa);
        String tipoIngreso = (mensualidad != null) ? "Mensualidad" : "Invitado";
        String numeroFolio = ticketDAO.generarNumeroFolio();

        // 2. Crear objeto Ticket
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setNumeroFolio(numeroFolio);
        nuevoTicket.setPlaca(placa);
        nuevoTicket.setTipoIngreso(tipoIngreso);
        nuevoTicket.setFechaIngreso(LocalDateTime.now());
        nuevoTicket.setOperadorIngresoId(operadorIngresoId);
        nuevoTicket.setActivo(true);
        nuevoTicket.setPagado(tipoIngreso.equals("Mensualidad"));
        nuevoTicket.setMontoCobrado(BigDecimal.ZERO);

        long timestamp = System.currentTimeMillis() / 1000;
        nuevoTicket.setQrCode("TICKET:" + numeroFolio + "|PLATE:" + placa + "|DATE:" + timestamp);


        // 3. Bloque Transaccional (Escritura)
        try {
            conn = DatabaseConfig.getInstance().getNewConnection();
            conn.setAutoCommit(false); // Iniciar Transacción

            // Guardar en base de datos usando la conexión transaccional
            ticketCreado = ticketDAO.create(nuevoTicket, conn); // <--- DEBE USAR 'conn'

            if (ticketCreado == null) {
                // Si el DAO retorna null, hubo un fallo en el INSERT. Lanzamos excepción para Rollback.
                throw new SQLException("Error al crear el ticket en la base de datos.");
            }

            conn.commit(); // Éxito: Guardar los cambios.
            System.out.println("✓ Ingreso registrado y transacción completada: " + placa + " - " + tipoIngreso);

        } catch (SQLException e) {
            // Fallo: Hacer Rollback
            System.err.println("⚠ Error en transacción 'registrarIngreso'. Ejecutando Rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Fallo al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            // Relanzar la excepción para que el Controller la maneje
            throw e;

        } finally {
            // Limpieza: Cerrar la conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar a auto-commit por si acaso
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Fallo al cerrar la conexión: " + closeEx.getMessage());
                }
            }
        }


        // 4. Imprimir ticket (Operación fuera de la transacción)
        if (ticketCreado != null) {
            try {
                Operador operador = operadorDAO.findById(operadorIngresoId); // Lectura fuera de la conexión transaccional
                if (operador != null) {
                    // Nota: Se asume que TicketPrinterService ya está implementado
                    TicketPrinterService printer = new TicketPrinterService(ticketCreado, operador.getNombre());
                    printer.imprimir();
                    System.out.println("✓ Ticket impreso exitosamente");
                }
            } catch (PrinterException e) {
                // Se registra el error de impresión pero se continúa, el ticket ya está en BD.
                System.err.println("⚠ Error al imprimir ticket. El ingreso se registró correctamente: " + e.getMessage());
            }
        }

        return ticketCreado;
    }

    /**
     * Registra la salida del vehículo aplicando las reglas de negocio.
     *
     * FLUJO:
     * 1. Buscar ticket abierto por placa
     * 2. Si es mensualidad → salida sin cobro
     * 3. Si es invitado → calcular tiempo y aplicar tarifa
     * 4. Aplicar tiempo de gracia (30 min por defecto)
     * 5. Registrar salida en BD
     * 6. Si hay cobro → crear registro de pago
     * 7. Actualizar ticket con monto y estado pagado
     *
     * @param placa Placa del vehículo
     * @param operadorSalidaId ID del operador que registra la salida
     * @param metodoPago Método de pago: "Efectivo", "Tarjeta", "Transferencia"
     * @return true si la salida se registró correctamente
     */
    public BigDecimal registrarSalida(String placa, int operadorSalidaId, String metodoPago) throws SQLException {
        Connection conn = null;
        BigDecimal monto = BigDecimal.ZERO; // Inicializar aquí para que sea visible en el bloque try

        try{
            conn = DatabaseConfig.getInstance().getNewConnection();
            conn.setAutoCommit(false); // 1. INICIO DE TRANSACCIÓN

            // Validaciones de entrada (Placa y Método de Pago)
            if (placa == null || placa.trim().isEmpty()) {
                throw new SQLException("La placa no puede estar vacía");
            }
            placa = placa.trim().toUpperCase();

            String metodoPagoFinal = metodoPago.trim();
            if (!metodoPagoFinal.equals("Efectivo") &&
                    !metodoPagoFinal.equals("Tarjeta") &&
                    !metodoPagoFinal.equals("Transferencia")) {
                throw new SQLException("Método de pago no válido. Use: Efectivo, Tarjeta o Transferencia");
            }

            // Lectura de ticket
            Ticket ticket = ticketDAO.findTicketAbiertoByPlaca(placa);
            if (ticket == null) {
                throw new SQLException("No se encontró un ticket abierto para la placa: " + placa);
            }
            if (ticket.getFechaSalida() != null) {
                throw new SQLException("Este ticket ya fue cerrado anteriormente");
            }

            // Calcular tiempo de estadía
            LocalDateTime ahora = LocalDateTime.now();
            long minutosEstadia = Duration.between(ticket.getFechaIngreso(), ahora).toMinutes();
            System.out.println("⏱ Tiempo de estadía: " + minutosEstadia + " minutos");

            // ✅ CASO 1: Si es mensualidad → salida sin cobro
            if (ticket.getTipoIngreso().equalsIgnoreCase("Mensualidad")) {
                boolean salidaRegistrada = ticketDAO.registrarSalida(
                        ticket.getId(),
                        operadorSalidaId,
                        (int) minutosEstadia,
                        conn
                );

                if (salidaRegistrada) {
                    ticketDAO.registrarPago(ticket.getId(), BigDecimal.ZERO, conn);
                    System.out.println("✓ Salida registrada (Mensualidad - sin cobro)");
                    conn.commit(); // 2. COMMIT para Mensualidad
                    return BigDecimal.ZERO; // 💡 Retorna 0.00
                } else {
                    // Si falla la salida (algo grave), forzamos rollback y relanzamos
                    throw new SQLException("Error grave al registrar la salida de la mensualidad.");
                }
            }

            // ✅ CASO 2: Es invitado → calcular cobro

            // Obtener la tarifa activa
            Tarifa tarifa = tarifaDAO.findTarifaActiva();
            if (tarifa == null) {
                throw new SQLException("No hay tarifa activa configurada en el sistema");
            }

            // Lógica de cálculo de tarifa (Tiempo de gracia, horas, fracción, tope diario)
            int tiempoGracia = tarifa.getTiempoGraciaMinutos();
            monto = BigDecimal.ZERO; // Reiniciamos monto para cálculo

            if (minutosEstadia > tiempoGracia) {
                long minutosCobrables = minutosEstadia - tiempoGracia;
                long horasCompletas = minutosCobrables / 60;
                long minutosRestantes = minutosCobrables % 60;

                if (horasCompletas > 0) {
                    monto = tarifa.getValorBaseHora().multiply(BigDecimal.valueOf(horasCompletas));
                }
                if (minutosRestantes > 0) {
                    monto = monto.add(tarifa.getValorFraccion());
                }
                if (tarifa.getTopeDiario() != null && monto.compareTo(tarifa.getTopeDiario()) > 0) {
                    monto = tarifa.getTopeDiario();
                }
                System.out.println("💵 Monto total a cobrar: $" + monto);
            } else {
                System.out.println("✓ Dentro del tiempo de gracia (" + tiempoGracia + " min) - Sin cobro");
                monto = BigDecimal.ZERO; // Asegura que el monto sea 0 si está en tiempo de gracia
            }


            // 3. REGISTRO DE SALIDA (Escritura 1/3)
            boolean salidaRegistrada = ticketDAO.registrarSalida(
                    ticket.getId(),
                    operadorSalidaId,
                    (int) minutosEstadia,
                    conn
            );

            if (!salidaRegistrada) {
                throw new SQLException("Error al registrar la fecha y operador de salida."); // Forzará Rollback
            }


            // 4. REGISTRO DE PAGO (Escritura 2/3)
            if (monto.compareTo(BigDecimal.ZERO) > 0) {
                Pago pago = new Pago();
                pago.setTicketId(ticket.getId());
                pago.setMonto(monto);
                pago.setMetodoPago(metodoPagoFinal);
                pago.setOperadorId(operadorSalidaId);
                pago.setFechaPago(ahora);
                pago.setObservaciones("Pago automático por salida - Tiempo: " + minutosEstadia + " min");

                Pago pagoCreado = pagoDAO.create(pago, conn);
                if (pagoCreado == null) {
                    throw new SQLException("Error al registrar el pago en la tabla 'pagos'. Se requiere ROLLBACK.");
                }
                System.out.println("✓ Pago registrado: $" + monto + " (" + metodoPagoFinal + ")");
            }


            // 5. ACTUALIZAR TICKET (Escritura 3/3)
            boolean pagoActualizado = ticketDAO.registrarPago(ticket.getId(), monto, conn);

            if (!pagoActualizado) {
                throw new SQLException("Error al actualizar el estado de pago del ticket. Se requiere ROLLBACK.");
            }

            conn.commit(); // 6. COMMIT para Invitado (Éxito total)

            System.out.println("✓ Salida registrada exitosamente - Placa: " + placa + " - Monto: $" + monto);
            return monto; // 💡 Retorna el monto calculado

        } catch (SQLException e) {
            // 7. ROLLBACK en caso de cualquier error
            System.err.println("⚠ Error en transacción 'registrarSalida'. Ejecutando Rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Fallo al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            // 8. Relanzar la excepción para que el Controller la maneje
            throw e;

        } finally {
            // 9. CERRAR CONEXIÓN SIEMPRE
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Fallo al cerrar la conexión: " + closeEx.getMessage());
                }
            }
        }
    }
    /**
     * Registra un pago manual (usado por operador desde el panel).
     */
    public boolean registrarPagoManual(String placa, BigDecimal monto, String metodoPago, int operadorId) throws SQLException {
        // Validar entrada
        if (placa == null || placa.trim().isEmpty()) {
            throw new SQLException("La placa no puede estar vacía");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SQLException("El monto debe ser mayor a cero");
        }

        placa = placa.trim().toUpperCase();

        Ticket ticket = ticketDAO.findTicketAbiertoByPlaca(placa);
        if (ticket == null) {
            throw new SQLException("No se encontró un ticket abierto para la placa: " + placa);
        }

        if (ticket.getTipoIngreso().equalsIgnoreCase("Mensualidad")) {
            throw new SQLException("El vehículo con mensualidad no requiere pago");
        }

        // Registrar pago manual
        Pago pago = new Pago();
        pago.setTicketId(ticket.getId());
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setOperadorId(operadorId);
        pago.setFechaPago(LocalDateTime.now());
        pago.setObservaciones("Pago manual registrado por operador");

        Pago pagoCreado = pagoDAO.create(pago);

        if (pagoCreado == null) {
            throw new SQLException("Error al registrar el pago manual");
        }

        // Marcar ticket como pagado
        boolean actualizado = ticketDAO.registrarPago(ticket.getId(), monto);

        if (actualizado) {
            System.out.println("✓ Pago manual registrado: $" + monto);
        }

        return actualizado;
    }
}