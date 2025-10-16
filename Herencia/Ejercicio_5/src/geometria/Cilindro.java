package geometria;

import java.util.Scanner;

public class Cilindro extends Circulo {
    protected double altura;

    public Cilindro() {
        super();
        this.altura = 0.0;
    }

    public Cilindro(double radio, double altura) {
        super(radio);
        this.altura = altura;
    }

    public void leerDatos(Scanner sc) {
        super.leerRadio(sc);
        System.out.print("Ingrese la altura del cilindro: ");
        this.altura = sc.nextDouble();
        sc.nextLine();
    }

    public double areaSuperficial() {
        return 2.0 * Math.PI * radio * altura + 2.0 * Math.PI * radio * radio;
    }

    public double volumen() {
        return Math.PI * radio * radio * altura;
    }

    @Override
    public void mostrar() {
        System.out.printf("--- CILINDRO ---%n");
        System.out.printf("Radio (base): %.4f%n", radio);
        System.out.printf("Altura: %.4f%n", altura);
        System.out.printf("Longitud de la circunferencia de la base: %.4f%n", longitud());
        System.out.printf("Área superficial total: %.4f%n", areaSuperficial());
        System.out.printf("Volumen: %.4f%n", volumen());
    }
}
