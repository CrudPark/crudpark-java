package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mensualidad {

    private int id;
    private String nombrePropietario;
    private String email;
    private String placa;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor vacío
    public Mensualidad() {
    }

    // Constructor completo
    public Mensualidad(int id, String nombrePropietario, String email, String placa,
                       LocalDate fechaInicio, LocalDate fechaFin, boolean activa,
                       LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombrePropietario = nombrePropietario;
        this.email = email;
        this.placa = placa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Constructor para nueva mensualidad (sin ID)
    public Mensualidad(String nombrePropietario, String email, String placa,
                       LocalDate fechaInicio, LocalDate fechaFin, boolean activa) {
        this.nombrePropietario = nombrePropietario;
        this.email = email;
        this.placa = placa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
    }

    // Método útil para verificar si está vigente
    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        return activa && !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
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
        return "Mensualidad{" +
                "id=" + id +
                ", nombrePropietario='" + nombrePropietario + '\'' +
                ", placa='" + placa + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", vigente=" + isVigente() +
                '}';
    }
}