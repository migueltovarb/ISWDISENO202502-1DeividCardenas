public abstract class Empleado extends Persona {
    protected int anioIncorporacion;
    protected String numDespacho;

    public Empleado(String nombre, String apellidos, String DNI, String estadoCivil, int anioIncorporacion, String numDespacho) {
        super(nombre, apellidos, DNI, estadoCivil);
        this.anioIncorporacion = anioIncorporacion;
        this.numDespacho = numDespacho;
    }

    public void reasignarDespacho(String nuevoDespacho) {
        this.numDespacho = nuevoDespacho;
    }
}