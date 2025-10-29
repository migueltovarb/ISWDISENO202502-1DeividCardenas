public class Main {
    public static void main(String[] args) {

        Coche miCoche = new Coche("ABC-123", 5);

        Camion miCamion = new Camion("XYZ-789");
        Remolque miRemolque = new Remolque(5000); // 5000 kg

        System.out.println("--- PRUEBA DEL COCHE ---");
        System.out.println(miCoche.toString());
        miCoche.acelerar(120);
        System.out.println(miCoche.toString());
        System.out.println("Puertas del coche: " + miCoche.getNumPuertas());

        System.out.println("\n--- PRUEBA DEL CAMIÓN (SIN REMOLQUE) ---");
        System.out.println(miCamion.toString());
        miCamion.acelerar(120);
        System.out.println(miCamion.toString());

        System.out.println("\n--- PRUEBA DEL CAMIÓN (CON REMOLQUE) ---");
        miCamion.ponRemolque(miRemolque);
        System.out.println(miCamion.toString());


        miCamion.velocidad = 0;
        System.out.println("Velocidad del camión reseteada a 0.");

        miCamion.acelerar(90);
        System.out.println(miCamion.toString());

        System.out.println("Intentando acelerar 20 km/h más...");
        miCamion.acelerar(20);

        System.out.println(miCamion.toString());
    }
}