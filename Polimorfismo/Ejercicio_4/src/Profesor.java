public class Profesor extends Empleado {
    private String departamento;

    public Profesor(String nombre, String apellidos, String DNI, String estadoCivil, int anioIncorporacion, String numDespacho, String departamento) {
        super(nombre, apellidos, DNI, estadoCivil, anioIncorporacion, numDespacho);
        this.departamento = departamento;
    }

    public void cambiarDepartamento(String nuevoDepartamento) {
        this.departamento = nuevoDepartamento;
    }

    @Override
    public void imprimirInformacion() {
        System.out.println("--- PROFESOR ---");
        System.out.println("Nombre: " + nombre + " " + apellidos);
        System.out.println("DNI: " + DNI + ", Estado Civil: " + estadoCivil);
        System.out.println("Año Incorp.: " + anioIncorporacion + ", Despacho: " + numDespacho);
        System.out.println("Departamento: " + departamento);
    }
}