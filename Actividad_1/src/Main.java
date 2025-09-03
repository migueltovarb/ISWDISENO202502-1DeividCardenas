import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final double SALDO_INICIAL = 1000.0; // constante
        double saldo = SALDO_INICIAL;       // variable que cambia
        Scanner scanner = new Scanner(System.in);

        int opcion = 0;

        while (opcion != 4) {
            // Menú
            System.out.println("\n--- Cajero Automático ---");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar dinero");
            System.out.println("3. Retirar dinero");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");

            // Leer opción
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
            } else {
                System.out.println("Opción inválida.");
                scanner.next(); // limpiar entrada
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("Saldo actual: $" + saldo);
                    break;

                case 2:
                    System.out.print("Ingresa la cantidad a depositar: ");
                    double deposito = scanner.nextDouble();
                    if (deposito > 0) {
                        saldo += deposito;
                        System.out.println("Depósito exitoso. Nuevo saldo: $" + saldo);
                    } else {
                        System.out.println("Cantidad inválida.");
                    }
                    break;

                case 3:
                    System.out.print("Ingresa la cantidad a retirar: ");
                    double retiro = scanner.nextDouble();
                    if (retiro > 0) {
                        if (retiro <= saldo) {
                            saldo -= retiro;
                            System.out.println("Retiro exitoso. Nuevo saldo: $" + saldo);
                        } else {
                            System.out.println("Error: saldo insuficiente.");
                        }
                    } else {
                        System.out.println("Cantidad inválida.");
                    }
                    break;

                case 4:
                    System.out.println("Gracias por usar el cajero. ¡Hasta luego!");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
    }
}
