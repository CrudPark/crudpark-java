package util;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Utilidad para el envío directo de datos a una impresora térmica.
 * Maneja la impresión directa a dispositivo en Linux y RAW en Windows.
 */
public class PrinterUtil {

    private static final String LINUX_PRINTER_PATH = "/dev/usb/lp0";
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Envía datos binarios (ESC/POS) a la impresora.
     * * @param data Los bytes a enviar a la impresora.
     * @param windowsPrinterName El nombre exacto de la impresora en Windows (necesario solo en Windows).
     */
    public static void printTicket(byte[] data, String windowsPrinterName) {
        if (OS.contains("win")) {
            printRawWindows(data, windowsPrinterName);
        } else if (OS.contains("nix") || OS.contains("mac") || OS.contains("linux")) {
            printRawLinux(data);
        } else {
            System.err.println(" Sistema operativo no soportado para impresión directa.");
        }
    }

    // Lógica para Linux (Impresión Directa a Archivo de Dispositivo)
    private static void printRawLinux(byte[] data) {
        File printer = new File(LINUX_PRINTER_PATH);

        if (!printer.exists()) {
            System.err.println("Error: No se encontró la impresora en " + LINUX_PRINTER_PATH);
            System.err.println("Verifica la conexión o los permisos. Intenta: sudo chmod 666 " + LINUX_PRINTER_PATH);
            return;
        }

        try (OutputStream out = new FileOutputStream(LINUX_PRINTER_PATH)) {
            out.write(data);
            out.flush();
            System.out.println("Ticket enviado a la impresora (Linux Directo).");
        } catch (Exception e) {
            System.err.println("Error al imprimir ticket en Linux: " + e.getMessage());
        }
    }

    // Lógica para Windows (Impresión RAW vía Nombre del Servicio)
    private static void printRawWindows(byte[] data, String printerName) {
        //Definir el formato de los datos: RAW
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        //Buscar todos los servicios de impresión disponibles
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, pras);

        PrintService selectedService = null;
        for (PrintService service : printServices) {
            // Comparar el nombre de la impresora ignorando mayúsculas/minúsculas
            if (service.getName().equalsIgnoreCase(printerName)) {
                selectedService = service;
                break;
            }
        }

        if (selectedService == null) {
            System.err.println("Error: La impresora '" + printerName + "' no se encontró en Windows.");
            System.err.println("Verifica el nombre exacto de la impresora en 'Dispositivos e Impresoras'.");
            return;
        }

        try {
            //Crear un trabajo de impresión (DocPrintJob)
            DocPrintJob job = selectedService.createPrintJob();

            //Crear el documento con los datos RAW
            Doc doc = new SimpleDoc(data, flavor, null);

            //Imprimir los datos RAW (ESC/POS)
            job.print(doc, pras);
            System.out.println("Ticket enviado a la impresora (Windows RAW).");

        } catch (PrintException e) {
            System.err.println("Error de impresión en Windows (javax.print): " + e.getMessage());
        }
    }
}