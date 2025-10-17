package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tarifa {

    private int id;
    private String nombre;
    private BigDecimal valorBaseHora;
    private BigDecimal valorFraccion;
    private BigDecimal topeDiario;
    private int tiempoGraciaMinutos;
    private boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor vacío
    public Tarifa() {
    }

    // Constructor completo
    public Tarifa(int id, String nombre, BigDecimal valorBaseHora,
                  BigDecimal valorFraccion, BigDecimal topeDiario,
                  int tiempoGraciaMinutos, boolean activa,
                  LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombre = nombre;
        this.valorBaseHora = valorBaseHora;
        this.valorFraccion = valorFraccion;
        this.topeDiario = topeDiario;
        this.tiempoGraciaMinutos = tiempoGraciaMinutos;
        this.activa = activa;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Constructor para nueva tarifa (sin ID)
    public Tarifa(String nombre, BigDecimal valorBaseHora, BigDecimal valorFraccion,
                  BigDecimal topeDiario, int tiempoGraciaMinutos, boolean activa) {
        this.nombre = nombre;
        this.valorBaseHora = valorBaseHora;
        this.valorFraccion = valorFraccion;
        this.topeDiario = topeDiario;
        this.tiempoGraciaMinutos = tiempoGraciaMinutos;
        this.activa = activa;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getValorBaseHora() {
        return valorBaseHora;
    }

    public void setValorBaseHora(BigDecimal valorBaseHora) {
        this.valorBaseHora = valorBaseHora;
    }

    public BigDecimal getValorFraccion() {
        return valorFraccion;
    }

    public void setValorFraccion(BigDecimal valorFraccion) {
        this.valorFraccion = valorFraccion;
    }

    public BigDecimal getTopeDiario() {
        return topeDiario;
    }

    public void setTopeDiario(BigDecimal topeDiario) {
        this.topeDiario = topeDiario;
    }

    public int getTiempoGraciaMinutos() {
        return tiempoGraciaMinutos;
    }

    public void setTiempoGraciaMinutos(int tiempoGraciaMinutos) {
        this.tiempoGraciaMinutos = tiempoGraciaMinutos;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
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

    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", valorBaseHora=" + valorBaseHora +
                ", tiempoGraciaMinutos=" + tiempoGraciaMinutos +
                ", activa=" + activa +
                '}';
    }
}