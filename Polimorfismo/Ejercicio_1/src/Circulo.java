
public class Circulo extends FiguraGeometrica {

    public Circulo(int radio) {
        super(radio);
    }

    @Override
    public double getArea() {
        return Math.PI * valor1 * valor1;
    }

    @Override
    public double getPerimetro() {
        return 2 * Math.PI * valor1;
    }
}