package geometria;

import java.util.Scanner;

public class CilindroHueco extends Cilindro {
    private double radioInterno;

    public CilindroHueco() {
        super();
        this.radioInterno = 0.0;
    }

    public CilindroHueco(double radioExterno, double radioInterno, double altura) {
        super(radioExterno, altura);
        this.radioInterno = radioInterno;
    }

    public void leerDatos(Scanner sc) {
        System.out.print("Ingrese el radio externo del cilindro hueco: ");
        this.radio = sc.nextDouble();
        System.out.print("Ingrese el radio interno del cilindro hueco: ");
        this.radioInterno = sc.nextDouble();
        System.out.print("Ingrese la altura del cilindro hueco: ");
        this.altura = sc.nextDouble();
        sc.nextLine();
    }

    public double areaSuperficial() {
        double areaLaterales = 2.0 * Math.PI * radio * altura + 2.0 * Math.PI * radioInterno * altura;
        double areaAnillos = 2.0 * Math.PI * (radio * radio - radioInterno * radioInterno);
        return areaLaterales + areaAnillos;
    }

    public double volumen() {
        return Math.PI * (radio * radio - radioInterno * radioInterno) * altura;
    }

    @Override
    public void mostrar() {
        System.out.printf("--- CILINDRO HUECO --- %n");
        System.out.printf("Radio externo: %.4f%n", radio);
        System.out.printf("Radio interno: %.4f%n", radioInterno);
        System.out.printf("Altura: %.4f%n", altura);
        System.out.printf("Longitud de la circunferencia externa: %.4f%n", 2.0 * Math.PI * radio);
        System.out.printf("Longitud de la circunferencia interna: %.4f%n", 2.0 * Math.PI * radioInterno);
        System.out.printf("Área superficial total: %.4f%n", areaSuperficial());
        System.out.printf("Volumen (espacio sólido entre radios): %.4f%n", volumen());
    }
}
