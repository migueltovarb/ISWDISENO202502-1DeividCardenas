
public class Cubo extends FiguraGeometrica {

    public Cubo(int arista) {
        super(arista);
    }

    @Override
    public double getArea() {
        return 6 * valor1 * valor1;
    }

    @Override
    public double getPerimetro() {
        return 0;
    }
}