public class Estudiante extends Persona {
    private String curso;

    public Estudiante(String nombre, String apellidos, String DNI, String estadoCivil, String curso) {
        super(nombre, apellidos, DNI, estadoCivil);
        this.curso = curso;
    }

    public void matricularNuevoCurso(String nuevoCurso) {
        this.curso = nuevoCurso;
    }

    @Override
    public void imprimirInformacion() {
        System.out.println("--- ESTUDIANTE ---");
        System.out.println("Nombre: " + nombre + " " + apellidos);
        System.out.println("DNI: " + DNI + ", Estado Civil: " + estadoCivil);
        System.out.println("Curso: " + curso);
    }
}