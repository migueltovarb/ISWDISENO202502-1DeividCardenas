
public class Vehiculo {

    protected String matricula;
    protected int velocidad;

    public Vehiculo(String matricula) {
        this.matricula = matricula;
        this.velocidad = 0;
    }

    public void acelerar(int cantidad) {
        this.velocidad += cantidad;
    }

    @Override
    public String toString() {
        return "Matrícula: " + this.matricula + ", Velocidad: " + this.velocidad + " km/h";
    }
}