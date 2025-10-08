package Veterinaria;

import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Repositorio repo = new Repositorio();
        VeterinariaService service = new VeterinariaService(repo);

        Dueno ana = service.registrarDueno("Ana García", "12345678", "+57 300 1112222");
        Dueno carlos = service.registrarDueno("Carlos Ruiz", "87654321", "+57 310 4443333");

        Mascota luna = service.registrarMascota("Luna", "Perro", 3, ana.getId());
        Mascota max = service.registrarMascota("Max", "Perro", 5, carlos.getId());

        try {
            service.registrarMascota("Luna", "Gato", 2, ana.getId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Error esperado al duplicar mascota: " + ex.getMessage());
        }

        service.registrarControl(luna.getId(), LocalDate.of(2025, 10, 1), "Vacuna", "Antirrábica");
        service.registrarControl(luna.getId(), LocalDate.of(2025, 10, 15), "Chequeo", "Revisión general");
        service.registrarControl(max.getId(), LocalDate.of(2025, 9, 20), "Desparasitación", "Por vía oral");

        try {
            service.registrarControl("id-no-existe", LocalDate.now(), "Vacuna", "Obs");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error esperado al registrar control en mascota inexistente: " + ex.getMessage());
        }

        System.out.println("\n--- HISTORIAL DE LUNA ---");
        service.historialMascota(luna.getId()).forEach(c -> System.out.println("  " + c));

        System.out.println("\n--- RESUMEN LUNA ---");
        System.out.println(service.resumenMascota(luna.getId()));

        System.out.println("\n--- RESUMEN GENERAL ---");
        for (Mascota m : repo.findAllMascotas()) {
            System.out.println(service.resumenMascota(m.getId()));
        }
    }

    static class Repositorio {
        private final Map<String, Dueno> duenos = new HashMap<>();
        private final Map<String, Mascota> mascotas = new HashMap<>();

        public void saveDueno(Dueno d) {
            if (d == null) throw new IllegalArgumentException("Dueño requerido");
            duenos.put(d.getId(), d);
        }
        public Optional<Dueno> findDuenoById(String id) { return Optional.ofNullable(duenos.get(id)); }
        public Collection<Dueno> findAllDuenos() { return Collections.unmodifiableCollection(duenos.values()); }

        public void saveMascota(Mascota m) {
            if (m == null) throw new IllegalArgumentException("Mascota requerida");
            mascotas.put(m.getId(), m);
        }
        public Optional<Mascota> findMascotaById(String id) { return Optional.ofNullable(mascotas.get(id)); }
        public Collection<Mascota> findAllMascotas() { return Collections.unmodifiableCollection(mascotas.values()); }

        public List<Mascota> findMascotasByDuenoId(String duenoId) {
            List<Mascota> out = new ArrayList<>();
            for (Mascota m : mascotas.values()) {
                if (m.getDueno() != null && m.getDueno().getId().equals(duenoId)) out.add(m);
            }
            return out;
        }
    }


    static class VeterinariaService {
        private final Repositorio repo;

        public VeterinariaService(Repositorio repo) { this.repo = repo; }

        public Dueno registrarDueno(String nombreCompleto, String documento, String telefono) {
            Dueno d = new Dueno(nombreCompleto, documento, telefono);
            repo.saveDueno(d);
            return d;
        }

        public Mascota registrarMascota(String nombre, String especie, int edad, String duenoId) {
            var opt = repo.findDuenoById(duenoId);
            if (opt.isEmpty()) throw new IllegalArgumentException("Dueño no encontrado: " + duenoId);
            Dueno dueno = opt.get();

            if (dueno.tieneMascotaConNombre(nombre)) {
                throw new IllegalArgumentException("El dueño ya tiene otra mascota con el mismo nombre: " + nombre);
            }

            Mascota m = new Mascota(nombre, especie, edad, dueno);
            repo.saveMascota(m);
            dueno.addMascota(m);
            return m;
        }

        public ControlVeterinario registrarControl(String mascotaId, LocalDate fecha, String tipo, String observaciones) {
            var opt = repo.findMascotaById(mascotaId);
            if (opt.isEmpty()) throw new IllegalArgumentException("Mascota no encontrada: " + mascotaId);
            Mascota m = opt.get();
            ControlVeterinario c = new ControlVeterinario(fecha, tipo, observaciones);
            m.addControl(c);
            return c;
        }

        public List<ControlVeterinario> historialMascota(String mascotaId) {
            var opt = repo.findMascotaById(mascotaId);
            if (opt.isEmpty()) throw new IllegalArgumentException("Mascota no encontrada: " + mascotaId);
            return opt.get().getControles();
        }

        public String resumenMascota(String mascotaId) {
            var opt = repo.findMascotaById(mascotaId);
            if (opt.isEmpty()) throw new IllegalArgumentException("Mascota no encontrada: " + mascotaId);
            Mascota m = opt.get();
            return String.format("Mascota: %s | Especie: %s | Controles realizados: %d",
                    m.getNombre(), m.getEspecie(), m.getCantidadControles());
        }
    }
}
