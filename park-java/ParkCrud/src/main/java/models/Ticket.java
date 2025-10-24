package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {

    private int id;
    private String numeroFolio;
    private String placa;
    private String tipoIngreso; // "Mensualidad" o "Invitado"
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private Integer operadorIngresoId;
    private Integer operadorSalidaId;
    private Integer tiempoEstadiaMinutos;
    private BigDecimal montoCobrado;
    private boolean pagado;
    private boolean activo;
    private String qrCode;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Relaciones (opcional, para joins)
    private Operador operadorIngreso;
    private Operador operadorSalida;

    // Constructor vacío
    public Ticket() {
    }

    // Constructor completo
    public Ticket(int id, String numeroFolio, String placa, String tipoIngreso,
                  LocalDateTime fechaIngreso, LocalDateTime fechaSalida,
                  Integer operadorIngresoId, Integer operadorSalidaId,
                  Integer tiempoEstadiaMinutos, BigDecimal montoCobrado,
                  boolean pagado, boolean activo, String qrCode,
                  LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.numeroFolio = numeroFolio;
        this.placa = placa;
        this.tipoIngreso = tipoIngreso;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.operadorIngresoId = operadorIngresoId;
        this.operadorSalidaId = operadorSalidaId;
        this.tiempoEstadiaMinutos = tiempoEstadiaMinutos;
        this.montoCobrado = montoCobrado;
        this.pagado = pagado;
        this.activo = activo;
        this.qrCode = qrCode;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Constructor para nuevo ingreso
    public Ticket(String numeroFolio, String placa, String tipoIngreso,
                  LocalDateTime fechaIngreso, Integer operadorIngresoId, String qrCode) {
        this.numeroFolio = numeroFolio;
        this.placa = placa;
        this.tipoIngreso = tipoIngreso;
        this.fechaIngreso = fechaIngreso;
        this.operadorIngresoId = operadorIngresoId;
        this.qrCode = qrCode;
        this.montoCobrado = BigDecimal.ZERO;
        this.pagado = false;
        this.activo = true;
    }

    // Método útil para verificar si está abierto
    public boolean isAbierto() {
        return fechaSalida == null && activo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroFolio() {
        return numeroFolio;
    }

    public void setNumeroFolio(String numeroFolio) {
        this.numeroFolio = numeroFolio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getOperadorIngresoId() {
        return operadorIngresoId;
    }

    public void setOperadorIngresoId(Integer operadorIngresoId) {
        this.operadorIngresoId = operadorIngresoId;
    }

    public Integer getOperadorSalidaId() {
        return operadorSalidaId;
    }

    public void setOperadorSalidaId(Integer operadorSalidaId) {
        this.operadorSalidaId = operadorSalidaId;
    }

    public Integer getTiempoEstadiaMinutos() {
        return tiempoEstadiaMinutos;
    }

    public void setTiempoEstadiaMinutos(Integer tiempoEstadiaMinutos) {
        this.tiempoEstadiaMinutos = tiempoEstadiaMinutos;
    }

    public BigDecimal getMontoCobrado() {
        return montoCobrado;
    }

    public void setMontoCobrado(BigDecimal montoCobrado) {
        this.montoCobrado = montoCobrado;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Operador getOperadorIngreso() {
        return operadorIngreso;
    }

    public void setOperadorIngreso(Operador operadorIngreso) {
        this.operadorIngreso = operadorIngreso;
    }

    public Operador getOperadorSalida() {
        return operadorSalida;
    }

    public void setOperadorSalida(Operador operadorSalida) {
        this.operadorSalida = operadorSalida;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", numeroFolio='" + numeroFolio + '\'' +
                ", placa='" + placa + '\'' +
                ", tipoIngreso='" + tipoIngreso + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                ", fechaSalida=" + fechaSalida +
                ", montoCobrado=" + montoCobrado +
                ", pagado=" + pagado +
                '}';
    }

}