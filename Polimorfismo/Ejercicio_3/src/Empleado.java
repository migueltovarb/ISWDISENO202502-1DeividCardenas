
public abstract class Empleado {
    protected String nombre;
    protected String apellidos;
    protected String DNI;
    protected String direccion;
    protected int antiguedadAnios;
    protected String telefono;
    protected double salario;
    protected Empleado supervisor;

    public Empleado(String nombre, String apellidos, String DNI, String direccion, String telefono, double salario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.DNI = DNI;
        this.direccion = direccion;
        this.telefono = telefono;
        this.salario = salario;
        this.antiguedadAnios = 0;
        this.supervisor = null;
    }

    public void imprimir() {
        System.out.println("Nombre: " + nombre + " " + apellidos);
        System.out.println("DNI: " + DNI + ", Tel: " + telefono);
        System.out.println("Salario: $" + String.format("%.2f", salario));
        if (supervisor != null) {
            System.out.println("Supervisor: " + supervisor.nombre + " " + supervisor.apellidos);
        } else {
            System.out.println("Supervisor: (Ninguno)");
        }
    }

    public void cambiarSupervisor(Empleado nuevoSupervisor) {
        this.supervisor = nuevoSupervisor;
        System.out.println("El supervisor de " + this.nombre + " ha sido cambiado a " + nuevoSupervisor.nombre);
    }

    public abstract void incrementarSalario();

    public double getSalario() {
        return salario;
    }
}