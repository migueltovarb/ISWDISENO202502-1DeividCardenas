package geometria;

import java.util.Scanner;

public class Circulo {
    protected double radio;

    public Circulo() {
        this.radio = 0.0;
    }

    public Circulo(double radio) {
        this.radio = radio;
    }

    public void leerRadio(Scanner sc) {
        System.out.print("Ingrese el radio del círculo: ");
        this.radio = sc.nextDouble();
        sc.nextLine(); 
    }

    public double longitud() {
        return 2.0 * Math.PI * radio;
    }

    public double area() {
        return Math.PI * radio * radio;
    }

    public void mostrar() {
        System.out.printf("--- CÍRCULO ---%n");
        System.out.printf("Radio: %.4f%n", radio);
        System.out.printf("Longitud de la circunferencia: %.4f%n", longitud());
        System.out.printf("Área: %.4f%n", area());
    }
}
