package editorial;
import java.util.Scanner;


public class Libro extends Publicacion {
    private int numPaginas;
    private int anioPublicacion;

    public Libro() {
        super();
        this.numPaginas = 0;
        this.anioPublicacion = 0;
    }

    @Override
    public void leerDatos(Scanner sc) {
        super.leerDatos(sc);
        System.out.print("Número de páginas: ");
        numPaginas = sc.nextInt();
        System.out.print("Año de publicación: ");
        anioPublicacion = sc.nextInt();
        sc.nextLine();
    }

    @Override
    public void mostrar() {
        System.out.println("\n--- LIBRO ---");
        super.mostrar();
        System.out.println("Número de páginas: " + numPaginas);
        System.out.println("Año de publicación: " + anioPublicacion);
    }
}
