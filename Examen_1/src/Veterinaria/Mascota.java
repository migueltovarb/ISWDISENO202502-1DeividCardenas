package Veterinaria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Mascota {
    private final String id;
    private String nombre;
    private String especie;
    private int edad;
    private Dueno dueno;
    private final List<ControlVeterinario> controles = new ArrayList<>();

    public Mascota(String nombre, String especie, int edad, Dueno dueno) {
        if (isBlank(nombre)) throw new IllegalArgumentException("Nombre de la mascota requerido");
        if (isBlank(especie)) throw new IllegalArgumentException("Especie requerida");
        if (edad < 0) throw new IllegalArgumentException("Edad no puede ser negativa");
        if (dueno == null) throw new IllegalArgumentException("Dueño requerido");
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre.trim();
        this.especie = especie.trim();
        this.edad = edad;
        this.dueno = dueno;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public int getEdad() { return edad; }
    public Dueno getDueno() { return dueno; }
    public List<ControlVeterinario> getControles() { return Collections.unmodifiableList(controles); }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setDueno(Dueno dueno) { this.dueno = dueno; }

    public void addControl(ControlVeterinario c) {
        if (c == null) throw new IllegalArgumentException("Control nulo");
        controles.add(c);
    }

    public int getCantidadControles() { return controles.size(); }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    public String toString() {
        return "Mascota: " + nombre +
               " (" + especie + ", " + edad + " años)" +
               " - Dueño: " + (dueno != null ? dueno.getNombreCompleto() : "Sin dueño") +
               " | Controles: " + getCantidadControles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mascota)) return false;
        Mascota mascota = (Mascota) o;
        return id.equals(mascota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
