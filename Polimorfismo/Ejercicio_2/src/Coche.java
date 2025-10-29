
public class Coche extends Vehiculo {
    private int numPuertas;

    public Coche(String matricula, int numPuertas) {
        super(matricula);
        this.numPuertas = numPuertas;
    }

    public int getNumPuertas() {
        return this.numPuertas;
    }

}