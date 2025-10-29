
public class Cuadrado extends FiguraGeometrica {

    public Cuadrado(int lado) {
        super(lado);
    }

    @Override
    public double getArea() {

        return valor1 * valor1;
    }

    @Override
    public double getPerimetro() {
        return 4 * valor1;
    }
}