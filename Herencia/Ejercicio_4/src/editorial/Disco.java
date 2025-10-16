package editorial;
import java.util.Scanner;


public class Disco extends Publicacion {
    private float duracion;
    public Disco() {
        super();
        this.duracion = 0.0f;
    }

    @Override
    public void leerDatos(Scanner sc) {
        super.leerDatos(sc);
        System.out.print("Duración (minutos): ");
        duracion = sc.nextFloat();
        sc.nextLine();
    }

    @Override
    public void mostrar() {
        System.out.println("\n--- DISCO ---");
        super.mostrar();
        System.out.println("Duración: " + duracion + " minutos");
    }
}
