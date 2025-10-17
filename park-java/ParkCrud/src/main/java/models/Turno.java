package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Turno {

    private int id;
    private Integer operadorId;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private int totalIngresos;
    private BigDecimal totalCobros;
    private boolean activo;
    private String observaciones;

    // Relación (opcional)
    private Operador operador;

    // Constructor vacío
    public Turno() {
    }

    // Constructor completo
    public Turno(int id, Integer operadorId, LocalDateTime fechaApertura,
                 LocalDateTime fechaCierre, int totalIngresos, BigDecimal totalCobros,
                 boolean activo, String observaciones) {
        this.id = id;
        this.operadorId = operadorId;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.totalIngresos = totalIngresos;
        this.totalCobros = totalCobros;
        this.activo = activo;
        this.observaciones = observaciones;
    }

    // Constructor para apertura de turno
    public Turno(Integer operadorId, LocalDateTime fechaApertura) {
        this.operadorId = operadorId;
        this.fechaApertura = fechaApertura;
        this.totalIngresos = 0;
        this.totalCobros = BigDecimal.ZERO;
        this.activo = true;
    }

    // Método útil para verificar si el turno está abierto
    public boolean isAbierto() {
        return activo && fechaCierre == null;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Integer operadorId) {
        this.operadorId = operadorId;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public int getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(int totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalCobros() {
        return totalCobros;
    }

    public void setTotalCobros(BigDecimal totalCobros) {
        this.totalCobros = totalCobros;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id=" + id +
                ", operadorId=" + operadorId +
                ", fechaApertura=" + fechaApertura +
                ", fechaCierre=" + fechaCierre +
                ", totalIngresos=" + totalIngresos +
                ", totalCobros=" + totalCobros +
                ", activo=" + activo +
                '}';
    }
}
