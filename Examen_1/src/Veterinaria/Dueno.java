package Veterinaria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

public class Dueno {
    private final String id;
    private String nombreCompleto;
    private String documento;
    private String telefono;
    private final List<Mascota> mascotas = new ArrayList<>();

    public Dueno(String nombreCompleto, String documento, String telefono) {
        if (isBlank(nombreCompleto)) throw new IllegalArgumentException("Nombre completo requerido");
        if (isBlank(documento)) throw new IllegalArgumentException("Documento requerido");
        if (isBlank(telefono)) throw new IllegalArgumentException("Teléfono requerido");
        this.id = UUID.randomUUID().toString();
        this.nombreCompleto = nombreCompleto.trim();
        this.documento = documento.trim();
        this.telefono = telefono.trim();
    }

    public String getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public List<Mascota> getMascotas() { return Collections.unmodifiableList(mascotas); }

    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void addMascota(Mascota m) {
        if (m == null) throw new IllegalArgumentException("Mascota nula");
        mascotas.add(m);
    }

    public boolean tieneMascotaConNombre(String nombre) {
        if (isBlank(nombre)) return false;
        String n = nombre.trim().toLowerCase();
        return mascotas.stream().anyMatch(m -> m.getNombre().trim().toLowerCase().equals(n));
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    @Override
    public String toString() {
        return "Dueño: " + nombreCompleto + " (Doc: " + documento + ", Tel: " + telefono + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dueno)) return false;
        Dueno dueno = (Dueno) o;
        return id.equals(dueno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
