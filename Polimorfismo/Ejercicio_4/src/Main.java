public class Main {
    public static void main(String[] args) {

        Estudiante est1 = new Estudiante("Mario", "Bros", "100A", "Soltero", "Curso 3");
        Profesor prof1 = new Profesor("Luigi", "Bros", "200B", "Casado", 2010, "A-101", "Matemáticas");
        PersonalDeServicio per1 = new PersonalDeServicio("Peach", "Toadstool", "300C", "Soltera", 2015, "B-202", "Biblioteca");

        Persona[] facultad = { est1, prof1, per1 };

        System.out.println("======= ESTADO INICIAL =======");
        for (Persona p : facultad) {
            p.imprimirInformacion();
            System.out.println("--------------------");
        }

        System.out.println("\n======= REALIZANDO CAMBIOS... =======");
        est1.matricularNuevoCurso("Curso 4");
        prof1.cambiarDepartamento("Lenguajes");
        per1.trasladarSeccion("Decanato");
        est1.cambiarEstadoCivil("Casado");
        prof1.reasignarDespacho("C-303");

        System.out.println("\n======= ESTADO FINAL =======");
        for (Persona p : facultad) {
            p.imprimirInformacion();
            System.out.println("--------------------");
        }
    }
}