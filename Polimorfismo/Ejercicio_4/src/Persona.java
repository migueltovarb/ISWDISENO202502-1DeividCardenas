public abstract class Persona {
    protected String nombre;
    protected String apellidos;
    protected String DNI;
    protected String estadoCivil;

    public Persona(String nombre, String apellidos, String DNI, String estadoCivil) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.DNI = DNI;
        this.estadoCivil = estadoCivil;
    }

    public void cambiarEstadoCivil(String nuevoEstado) {
        this.estadoCivil = nuevoEstado;
    }

    public abstract void imprimirInformacion();
}