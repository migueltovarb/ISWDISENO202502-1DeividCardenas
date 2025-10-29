
public abstract class FiguraGeometrica {

    protected int valor1;
    public abstract double getArea();
    public abstract double getPerimetro();

    public FiguraGeometrica(int valor1) {
        this.valor1 = valor1;
    }

    protected int getValor1() {
        return valor1;
    }

    protected void setValor1(int valor1) {
        this.valor1 = valor1;
    }

}