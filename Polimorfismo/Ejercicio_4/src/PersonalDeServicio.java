public class PersonalDeServicio extends Empleado {
    private String seccion;

    public PersonalDeServicio(String nombre, String apellidos, String DNI, String estadoCivil, int anioIncorporacion, String numDespacho, String seccion) {
        super(nombre, apellidos, DNI, estadoCivil, anioIncorporacion, numDespacho);
        this.seccion = seccion;
    }

    public void trasladarSeccion(String nuevaSeccion) {
        this.seccion = nuevaSeccion;
    }

    @Override
    public void imprimirInformacion() {
        System.out.println("--- PERSONAL DE SERVICIO ---");
        System.out.println("Nombre: " + nombre + " " + apellidos);
        System.out.println("DNI: " + DNI + ", Estado Civil: " + estadoCivil);
        System.out.println("Año Incorp.: " + anioIncorporacion + ", Despacho: " + numDespacho);
        System.out.println("Sección: " + seccion);
    }
}