package editorial;
import java.util.Scanner;


public class Publicacion {
    protected String titulo;
    protected float precio;

    public Publicacion(String titulo, float precio) {
        this.titulo = titulo;
        this.precio = precio;
    }

    public Publicacion() {
        this.titulo = "";
        this.precio = 0.0f;
    }

    public void leerDatos(Scanner sc) {
        System.out.print("Título: ");
        titulo = sc.nextLine();
        System.out.print("Precio: ");
        precio = sc.nextFloat();
        sc.nextLine();
    }

    public void mostrar() {
        System.out.println("Título: " + titulo);
        System.out.println("Precio: $" + precio);
    }
}
