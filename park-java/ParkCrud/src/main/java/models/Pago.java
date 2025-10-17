package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {

    private int id;
    private Integer ticketId;
    private BigDecimal monto;
    private String metodoPago; // "Efectivo", "Tarjeta", "Transferencia"
    private Integer operadorId;
    private LocalDateTime fechaPago;
    private String observaciones;

    // Relaciones (opcional)
    private Ticket ticket;
    private Operador operador;

    // Constructor vacío
    public Pago() {
    }

    // Constructor completo
    public Pago(int id, Integer ticketId, BigDecimal monto, String metodoPago,
                Integer operadorId, LocalDateTime fechaPago, String observaciones) {
        this.id = id;
        this.ticketId = ticketId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.operadorId = operadorId;
        this.fechaPago = fechaPago;
        this.observaciones = observaciones;
    }

    // Constructor para nuevo pago
    public Pago(Integer ticketId, BigDecimal monto, String metodoPago,
                Integer operadorId, String observaciones) {
        this.ticketId = ticketId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.operadorId = operadorId;
        this.observaciones = observaciones;
        this.fechaPago = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Integer operadorId) {
        this.operadorId = operadorId;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", fechaPago=" + fechaPago +
                '}';
    }
}