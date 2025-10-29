import java.util.ArrayList;
import java.util.List;


public class Vendedor extends Empleado {
    private String matriculaCoche;
    private String marcaCoche;
    private String modeloCoche;
    private String telMovil;
    private String areaVenta;
    private List<String> listaClientes;
    private double porcentajeComision;

    public Vendedor(String nombre, String apellidos, String DNI, String direccion, String telefono, double salario, String telMovil, String areaVenta, double comision) {
        super(nombre, apellidos, DNI, direccion, telefono, salario);
        this.telMovil = telMovil;
        this.areaVenta = areaVenta;
        this.porcentajeComision = comision;
        this.listaClientes = new ArrayList<>();
    }

    @Override
    public void imprimir() {
        System.out.println("** PUESTO: VENDEDOR **");
        super.imprimir();
        System.out.println("Móvil: " + this.telMovil + ", Área: " + this.areaVenta);
        System.out.println("Coche: " + this.marcaCoche + " " + this.modeloCoche + " (" + this.matriculaCoche + ")");
        System.out.println("Clientes: " + this.listaClientes.size());
    }

    @Override
    public void incrementarSalario() {
        double incremento = this.salario * 0.10;
        this.salario += incremento;
    }

    public void darDeAltaCliente(String nombreCliente) {
        this.listaClientes.add(nombreCliente);
        System.out.println("Cliente '" + nombreCliente + "' asignado a " + this.nombre);
    }

    public void darDeBajaCliente(String nombreCliente) {
        this.listaClientes.remove(nombreCliente);
    }

    public void cambiarCoche(String matricula, String marca, String modelo) {
        this.matriculaCoche = matricula;
        this.marcaCoche = marca;
        this.modeloCoche = modelo;
    }
}