package geometria;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Circulo c = new Circulo();
        System.out.println("DATOS PARA EL CÍRCULO:");
        c.leerRadio(sc);

        Cilindro cilindro = new Cilindro();
        System.out.println("\nDATOS PARA EL CILINDRO:");
        cilindro.leerDatos(sc);

        CilindroHueco ch = new CilindroHueco();
        System.out.println("\nDATOS PARA EL CILINDRO HUECO:");
        ch.leerDatos(sc);

        System.out.println("\n====== RESULTADOS ======");
        c.mostrar();
        System.out.println();
        cilindro.mostrar();
        System.out.println();
        ch.mostrar();

        sc.close();
    }
}
