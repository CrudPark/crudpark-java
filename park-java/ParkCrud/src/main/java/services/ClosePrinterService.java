package services;

import models.Ticket;
import util.PrinterUtil; // Utilidad multiplataforma
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime; // Necesario para obtener la hora actual si getFechaSalida() es null

/**
 * Servicio para generar el contenido del ticket de SALIDA/PAGO con comandos ESC/POS
 * y enviarlo a la impresora térmica utilizando la utilidad multi-plataforma.
 */
public class ClosePrinterService {

    private final Ticket ticket;
    private final int operadorSalidaId;
    private final String metodoPago;
    private final BigDecimal montoPagado;
    private final long minutoEstadia;
    private final LocalDateTime fechaSalidaReal; // Almacena la fecha de salida verificada

    //  IMPORTANTE: Define aquí el nombre exacto de tu impresora en Windows.
    // **DEBES ACTUALIZAR ESTE VALOR con el nombre de tu driver.**
    private static final String WINDOWS_PRINTER_NAME = "Nombre Exacto de la Impresora Termica";

    // Comandos ESC/POS (Bytes para control de la impresora)
    private static final byte[] INIT = {0x1B, 0x40};       // Inicializa (reset) impresora
    private static final byte[] CENTER = {0x1B, 0x61, 0x01}; // Centra texto
    private static final byte[] LEFT = {0x1B, 0x61, 0x00};   // Alinea a la izquierda
    private static final byte[] BOLD_ON = {0x1B, 0x45, 0x01};  // Negrita ON
    private static final byte[] BOLD_OFF = {0x1B, 0x45, 0x00}; // Negrita OFF
    private static final byte[] DOUBLE_HEIGHT = {0x1D, 0x21, 0x01}; // Doble altura
    private static final byte[] NORMAL_FONT = {0x1D, 0x21, 0x00}; // Fuente normal
    private static final byte[] CUT = {0x1D, 0x56, 0x00};     // Corte de papel completo

    // CONSTRUCTOR CORREGIDO: El nombre del constructor debe coincidir con el nombre de la clase
    public ClosePrinterService(Ticket ticket, int operadorSalidaId, String metodoPago, BigDecimal montoPagado, long minutosEstadia) {
        this.ticket = ticket;
        this.operadorSalidaId = operadorSalidaId;
        this.metodoPago = metodoPago;
        this.montoPagado = montoPagado;
        this.minutoEstadia = minutosEstadia;

        // CORRECCIÓN PRINCIPAL: Usamos el valor del Ticket si está seteado,
        // de lo contrario, asumimos que la salida es AHORA (en el momento de la impresión).
        this.fechaSalidaReal = ticket.getFechaSalida() != null ? ticket.getFechaSalida() : LocalDateTime.now();
    }

    private byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Genera el contenido completo del ticket de SALIDA/PAGO, incluyendo comandos ESC/POS.
     * @return Array de bytes listo para ser enviado a la impresora.
     */
    private byte[] generarTicketData() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Formato para el monto con 2 decimales
        String montoF = String.format("%.2f", montoPagado);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        // Inicialización y Encabezado
        bos.write(INIT);
        bos.write(CENTER);
        bos.write(NORMAL_FONT);

        bos.write(toBytes("==============================\n"));
        bos.write(BOLD_ON);
        bos.write(DOUBLE_HEIGHT);
        bos.write(toBytes("CRUD PARK - COMPROBANTE\n"));
        bos.write(BOLD_OFF);
        bos.write(NORMAL_FONT);
        bos.write(toBytes("==============================\n"));
        bos.write(toBytes("\n"));

        // Detalles del Ticket (Alineación izquierda)
        bos.write(LEFT);
        bos.write(toBytes(String.format("TICKET #: %s\n", ticket.getNumeroFolio())));
        bos.write(toBytes(String.format("PLACA: %s\n", ticket.getPlaca())));
        bos.write(toBytes(String.format("Tipo: %s\n", ticket.getTipoIngreso())));
        bos.write(toBytes("------------------------------\n"));

        // FECHAS Y ESTADÍA
        String fechaIngreso = ticket.getFechaIngreso().format(formatter);
        // Usamos la fechaSalidaReal (garantizada de no ser null)
        String fechaSalida = this.fechaSalidaReal.format(formatter);

        bos.write(toBytes(String.format("INGRESO: %s\n", fechaIngreso)));
        bos.write(toBytes(String.format("SALIDA:  %s\n", fechaSalida)));
        bos.write(toBytes(String.format("ESTADÍA: %d min\n", minutoEstadia)));
        bos.write(toBytes(String.format("OPERADOR: %d\n", operadorSalidaId)));

        bos.write(toBytes("==============================\n"));

        // MONTO COBRADO (Destacado)
        bos.write(CENTER);
        bos.write(BOLD_ON);
        bos.write(DOUBLE_HEIGHT);
        bos.write(toBytes(String.format("PAGO: $%s\n", montoF)));
        bos.write(BOLD_OFF);
        bos.write(NORMAL_FONT);
        bos.write(toBytes(String.format("MÉTODO: %s\n", metodoPago)));

        bos.write(toBytes("==============================\n"));

        // Mensaje Final y QR (Si es necesario)
        bos.write(CENTER);
        bos.write(toBytes("Gracias por su visita.\n"));
        bos.write(toBytes("QR de Salida: " + ticket.getQrCode() + "\n"));
        bos.write(toBytes("==============================\n"));

        // Espaciado y Corte
        bos.write(toBytes("\n\n\n\n")); // Espacio para sacar el papel del cabezal
        bos.write(CUT);

        return bos.toByteArray();
    }

    /**
     * Método que inicia la impresión enviando los comandos ESC/POS.
     */
    public void imprimir() {
        try {
            byte[] ticketData = generarTicketData();
            // Llama a la utilidad con los datos del ticket y el nombre de la impresora para Windows
            PrinterUtil.printTicket(ticketData, WINDOWS_PRINTER_NAME);
        } catch (IOException e) {
            System.err.println("Error al generar datos del ticket: " + e.getMessage());
        }
    }
}