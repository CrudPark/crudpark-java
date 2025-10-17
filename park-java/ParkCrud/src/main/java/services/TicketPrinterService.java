package services;

import models.Ticket;

import javax.print.*;
import java.awt.print.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para imprimir tickets en impresora térmica.
 */
public class TicketPrinterService implements Printable {

    private final Ticket ticket;
    private final String operadorNombre;

    public TicketPrinterService(Ticket ticket, String operadorNombre) {
        this.ticket = ticket;
        this.operadorNombre = operadorNombre;
    }

    /**
     * Método que Java usa para dibujar lo que se imprimirá.
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int y = 10;
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));

        g.drawString("==============================", 10, y += 10);
        g.drawString("     CrudPark - Crudzaso", 10, y += 15);
        g.drawString("==============================", 10, y += 15);
        g.drawString("Ticket #: " + ticket.getNumeroFolio(), 10, y += 15);
        g.drawString("Placa: " + ticket.getPlaca(), 10, y += 15);
        g.drawString("Tipo: " + ticket.getTipoIngreso(), 10, y += 15);

        String fechaIngreso = ticket.getFechaIngreso()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        g.drawString("Ingreso: " + fechaIngreso, 10, y += 15);
        g.drawString("Operador: " + operadorNombre, 10, y += 15);

        g.drawString("------------------------------", 10, y += 15);
        g.drawString("QR: " + ticket.getQrCode(), 10, y += 15);
        g.drawString("------------------------------", 10, y += 15);
        g.drawString("Gracias por su visita.", 10, y += 15);
        g.drawString("==============================", 10, y += 15);

        return PAGE_EXISTS;
    }

    /**
     * Método que inicia la impresión automática.
     */
    public void imprimir() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        // Puedes mostrar el diálogo de impresión o hacerlo automático:
        // job.printDialog(); // ← Muestra el diálogo
        job.print(); // ← Imprime directamente
    }
}
