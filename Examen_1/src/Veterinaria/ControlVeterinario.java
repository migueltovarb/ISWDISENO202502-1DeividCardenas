package Veterinaria;

import java.time.LocalDate;
import java.util.Objects;

public class ControlVeterinario {
    private final String id;
    private LocalDate fecha;
    private String tipo;
    private String observaciones;

    public ControlVeterinario(LocalDate fecha, String tipo, String observaciones) {
        if (fecha == null) throw new IllegalArgumentException("Fecha requerida");
        if (tipo == null || tipo.trim().isEmpty()) throw new IllegalArgumentException("Tipo de control requerido");
        this.id = java.util.UUID.randomUUID().toString();
        this.fecha = fecha;
        this.tipo = tipo.trim();
        this.observaciones = observaciones == null ? "" : observaciones.trim();
    }

    public String getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public String getObservaciones() { return observaciones; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Control [" + tipo + "] - Fecha: " + fecha + " | Obs: " + observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlVeterinario)) return false;
        ControlVeterinario that = (ControlVeterinario) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
