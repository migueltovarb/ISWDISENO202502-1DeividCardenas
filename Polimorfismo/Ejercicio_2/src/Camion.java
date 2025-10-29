
public class Camion extends Vehiculo {
    private Remolque remolque;

    public Camion(String matricula) {
        super(matricula);
        this.remolque = null;
    }

    public void ponRemolque(Remolque remolque) {
        this.remolque = remolque;
        System.out.println("Remolque acoplado al camión " + this.matricula);
    }

    public void quitaRemolque() {
        this.remolque = null;
        System.out.println("Remolque desacoplado del camión " + this.matricula);
    }

    @Override
    public void acelerar(int cantidad) {
        if (this.remolque != null && (this.velocidad + cantidad > 100)) {
            System.out.println("¡DEMASIADO RÁPIDO! El camión " + this.matricula + " no puede superar los 100 km/h con remolque.");
        } else {
            super.acelerar(cantidad);
        }
    }

    @Override
    public String toString() {

        String infoBase = super.toString();

        if (this.remolque != null) {
            return infoBase + "\n -> " + this.remolque.toString();
        } else {
            return infoBase + "\n -> (Sin remolque)";
        }
    }
}