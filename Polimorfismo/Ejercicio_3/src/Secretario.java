
public class Secretario extends Empleado {
    private String despacho;
    private String numFax;

    public Secretario(String nombre, String apellidos, String DNI, String direccion, String telefono, double salario, String despacho, String numFax) {
        super(nombre, apellidos, DNI, direccion, telefono, salario);
        this.despacho = despacho;
        this.numFax = numFax;
    }

    @Override
    public void imprimir() {
        System.out.println("** PUESTO: SECRETARIO/A **");
        super.imprimir();
        System.out.println("Despacho: " + this.despacho + ", Fax: " + this.numFax);
    }


    @Override
    public void incrementarSalario() {
        double incremento = this.salario * 0.05;
        this.salario += incremento;
    }
}