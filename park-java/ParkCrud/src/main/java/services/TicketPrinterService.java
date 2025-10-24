package services;

import models.Ticket;
import util.PrinterUtil; // <-- Ajustado para usar el paquete 'util'

import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servicio para generar el contenido del ticket con comandos ESC/POS
 * y enviarlo a la impresora térmica utilizando la utilidad multi-plataforma.
 */
public class TicketPrinterService {

    private final Ticket ticket;
    private final String operadorNombre;

    //  IMPORTANTE: Define aquí el nombre exacto de tu impresora en Windows.
    // Ejemplos: "EPSON TM-T88V Receipt", "Generic Thermal Printer"
    private static final String WINDOWS_PRINTER_NAME = "Nombre Exacto de la Impresora Termica";

    // Comandos ESC/POS (Bytes para control de la impresora)
    private static final byte[] INIT = {0x1B, 0x40};       // Inicializa (reset) impresora
    private static final byte[] CENTER = {0x1B, 0x61, 0x01}; // Centra texto
    private static final byte[] LEFT = {0x1B, 0x61, 0x00};   // Alinea a la izquierda
    private static final byte[] BOLD_ON = {0x1B, 0x45, 0x01};  // Negrita ON
    private static final byte[] BOLD_OFF = {0x1B, 0x1B, 0x45, 0x00}; // Negrita OFF y Desinicializar
    private static final byte[] DOUBLE_HEIGHT = {0x1D, 0x21, 0x01}; // Doble altura
    private static final byte[] NORMAL_FONT = {0x1D, 0x21, 0x00}; // Fuente normal
    private static final byte[] CUT = {0x1D, 0x56, 0x00};     // Corte de papel completo

    public TicketPrinterService(Ticket ticket, String operadorNombre) {
        this.ticket = ticket;
        this.operadorNombre = operadorNombre;
    }

    private byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Genera el contenido completo del ticket, incluyendo comandos ESC/POS.
     * @return Array de bytes listo para ser enviado a la impresora.
     */
    private byte[] generarTicketData() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Inicialización y Encabezado
        bos.write(INIT);
        bos.write(CENTER);
        bos.write(NORMAL_FONT);

        bos.write(toBytes("==============================\n"));
        bos.write(BOLD_ON);
        bos.write(DOUBLE_HEIGHT);
        bos.write(toBytes("CrudPark - Crudzaso\n"));
        bos.write(BOLD_OFF);
        bos.write(NORMAL_FONT);
        bos.write(toBytes("==============================\n"));
        bos.write(toBytes("\n"));

        // Detalles del Ticket (Alineación izquierda)
        bos.write(LEFT);
        bos.write(toBytes(String.format("Ticket #: %s\n", ticket.getNumeroFolio())));
        bos.write(toBytes(String.format("Placa: %s\n", ticket.getPlaca())));
        bos.write(toBytes(String.format("Tipo: %s\n", ticket.getTipoIngreso())));

        String fechaIngreso = ticket.getFechaIngreso()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        bos.write(toBytes(String.format("Ingreso: %s\n", fechaIngreso)));
        bos.write(toBytes(String.format("Operador: %s\n", operadorNombre)));

        bos.write(toBytes("------------------------------\n"));

        // QR (Centrado y Negrita para destacarlo)
        bos.write(CENTER);
        bos.write(BOLD_ON);
        bos.write(toBytes("QR: " + ticket.getQrCode() + "\n"));
        bos.write(BOLD_OFF);

        bos.write(CENTER);
        bos.write(toBytes("------------------------------\n"));

        // Mensaje Final
        bos.write(CENTER);
        bos.write(toBytes("Gracias por su visita.\n"));
        bos.write(toBytes("==============================\n"));

        // Espaciado y Corte
        bos.write(toBytes("\n\n\n")); // Espacio para sacar el papel del cabezal
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
