import java.util.ArrayList;
import java.util.List;

public class JefeDeZona extends Empleado {
    private String despacho;
    private Secretario secretario;
    private List<Vendedor> listaVendedores;
    private String matriculaCoche;
    private String marcaCoche;
    private String modeloCoche;

    public JefeDeZona(String nombre, String apellidos, String DNI, String direccion, String telefono, double salario, String despacho) {
        super(nombre, apellidos, DNI, direccion, telefono, salario);
        this.despacho = despacho;
        this.listaVendedores = new ArrayList<>();
    }

    @Override
    public void imprimir() {
        System.out.println("** PUESTO: JEFE DE ZONA **");
        super.imprimir();
        System.out.println("Despacho: " + this.despacho);
        if (this.secretario != null) {
            System.out.println("Secretario a cargo: " + this.secretario.nombre);
        }
        System.out.println("Vendedores a cargo: " + this.listaVendedores.size());
    }

    @Override
    public void incrementarSalario() {
        double incremento = this.salario * 0.20;
        this.salario += incremento;
    }

    public void cambiarSecretario(Secretario nuevoSecretario) {
        this.secretario = nuevoSecretario;
    }

    public void cambiarCoche(String matricula, String marca, String modelo) {
        this.matriculaCoche = matricula;
        this.marcaCoche = marca;
        this.modeloCoche = modelo;
    }

    public void darDeAltaVendedor(Vendedor vendedor) {
        this.listaVendedores.add(vendedor);
    }

    public void darDeBajaVendedor(Vendedor vendedor) {
        this.listaVendedores.remove(vendedor);
    }
}