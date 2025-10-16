package editorial;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Libro libro = new Libro();
        System.out.println("Ingrese los datos del libro:");
        libro.leerDatos(sc);

        Disco disco = new Disco();
        System.out.println("\nIngrese los datos del disco:");
        disco.leerDatos(sc);

        System.out.println("\n===== FICHAS REGISTRADAS =====");
        libro.mostrar();
        disco.mostrar();

        sc.close();
    }
}
