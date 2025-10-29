public class Main {
    public static void main(String[] args) {

        FiguraGeometrica[] figuras = new FiguraGeometrica[5];

        figuras[0] = new Circulo(10);
        figuras[1] = new Cuadrado(5);
        figuras[2] = new Rectangulo(4, 8);
        figuras[3] = new Triangulo(3, 4);
        figuras[4] = new Cubo(6);

        System.out.println("--- Demostración de Polimorfismo ---");

        for (FiguraGeometrica fig : figuras) {

            System.out.println("---------------------------------");
            System.out.println("Figura: " + fig.getClass().getSimpleName());
            System.out.println("Área: " + fig.getArea());
            System.out.println("Perímetro: " + fig.getPerimetro());
        }
    }
}