
public class Triangulo extends FiguraGeometrica {

    protected int valor2;

    public Triangulo(int base, int altura) {
        super(base);
        this.valor2 = altura;
    }

    @Override
    public double getArea() {
        return (valor1 * valor2) / 2.0;
    }

    @Override
    public double getPerimetro() {
        double hipotenusa = Math.sqrt(valor1 * valor1 + valor2 * valor2);
        return valor1 + valor2 + hipotenusa;
    }

    protected int getValor2() {
        return valor2;
    }

    protected void setValor2(int valor2) {
        this.valor2 = valor2;
    }
}