/* En el archivo se presenta el desarollo a la historia de usuario 6. Se tiene en cuenta que el programa
* se sigue ejecutando despues de haber solicitado una opcion, es decir después de
* presionar el botno 1 o 2 el sistema se regresa automaticamente a la opción 3 para seguir siendo ejecutada, hasta que
* el usuario decida salir del sistema presionando el boton 4*/
import java.util.Scanner;

public class controlAsistencia {

    /* Constante definida de acuerdo a la historia de usuario con 5 DIAS_SEMANA y 4 NUM_ESTUDIANTES*/
    public static final int DIAS_SEMANA = 5;
    public static final int NUM_ESTUDIANTES = 4;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char[][] asistencia = new char[NUM_ESTUDIANTES][DIAS_SEMANA];

        // Registrar asistencia por primera vez
        System.out.println("=== Registro inicial de asistencia ===");
        registrarAsistencia(scanner, asistencia);

        boolean salir = false;

        while (!salir) {
            int opcion = 0;
            boolean opcionValida;

            // Mostrar menú y validar entrada
            do {
                System.out.println("\n--- Menú ---");
                System.out.println("1. Ver asistencia individual");
                System.out.println("2. Ver resumen general");
                System.out.println("3. Volver a registrar asistencia");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción (1-4): ");

                opcionValida = true;
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine();
                    if (opcion < 1 || opcion > 4) {
                        System.out.println("Opción fuera de rango. Intente de nuevo.");
                        opcionValida = false;
                    }
                } else {
                    System.out.println("Entrada inválida. Debe ingresar un número del 1 al 4.");
                    scanner.nextLine();
                    opcionValida = false;
                }
            } while (!opcionValida);

            // Procesar opción seleccionada
            switch (opcion) {
                case 1:
                    verAsistenciaIndividual(asistencia);
                    break;
                case 2:
                    verResumenGeneral(asistencia);
                    break;
                case 3:
                    System.out.println("\n=== Volviendo a registrar asistencia ===");
                    registrarAsistencia(scanner, asistencia);
                    break;
                case 4:
                    salir = true;
                    System.out.println("\nSaliendo del sistema...");
                    break;
            }
        }

        scanner.close();
    }

    // Registrar asistencia por estudiante y día
    public static void registrarAsistencia(Scanner scanner, char[][] asistencia) {
        for (int estudiante = 0; estudiante < NUM_ESTUDIANTES; estudiante++) {
            System.out.println("\nEstudiante " + (estudiante + 1) + ":");

            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                boolean entradaValida;
                do {
                    System.out.print("  Día " + (dia + 1) + " (P = presente, A = ausente): ");
                    String entrada = scanner.nextLine().trim().toUpperCase();

                    entradaValida = entrada.length() == 1 && (entrada.charAt(0) == 'P' || entrada.charAt(0) == 'A');

                    if (!entradaValida) {
                        System.out.println("Entrada inválida. Use solo 'P' (presente) o 'A' (ausente).");
                    } else {
                        asistencia[estudiante][dia] = entrada.charAt(0);
                    }
                } while (!entradaValida);
            }
        }
    }

    // Mostrar asistencia por estudiante
    public static void verAsistenciaIndividual(char[][] asistencia) {
        System.out.println("\nAsistencia individual:");
        for (int estudiante = 0; estudiante < NUM_ESTUDIANTES; estudiante++) {
            int total = 0;
            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                if (asistencia[estudiante][dia] == 'P') {
                    total++;
                }
            }
            System.out.println("Estudiante " + (estudiante + 1) + ": " + total + " asistencias.");
        }
    }

    // Mostrar resumen general
    public static void verResumenGeneral(char[][] asistencia) {
        System.out.println("\nResumen General");

        // Estudiantes con asistencia completa
        System.out.println("Estudiantes que asistieron todos los días:");
        boolean alguno = false;
        for (int estudiante = 0; estudiante < NUM_ESTUDIANTES; estudiante++) {
            boolean todosPresentes = true;
            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                if (asistencia[estudiante][dia] != 'P') {
                    todosPresentes = false;
                    break;
                }
            }
            if (todosPresentes) {
                System.out.println(" - Estudiante " + (estudiante + 1));
                alguno = true;
            }
        }
        if (!alguno) {
            System.out.println("Ninguno");
        }

        // Calcular ausencias por día
        int[] ausenciasPorDia = new int[DIAS_SEMANA];
        for (int dia = 0; dia < DIAS_SEMANA; dia++) {
            for (int estudiante = 0; estudiante < NUM_ESTUDIANTES; estudiante++) {
                if (asistencia[estudiante][dia] == 'A') {
                    ausenciasPorDia[dia]++;
                }
            }
        }

        int[][] diasConAusencias = new int[DIAS_SEMANA][2];
        for (int i = 0; i < DIAS_SEMANA; i++) {
            diasConAusencias[i][0] = i + 1;
            diasConAusencias[i][1] = ausenciasPorDia[i]; // Número de ausencias
        }

        for (int i = 0; i < DIAS_SEMANA - 1; i++) {
            for (int j = i + 1; j < DIAS_SEMANA; j++) {
                if (diasConAusencias[i][1] > diasConAusencias[j][1]) {
                    int[] temp = diasConAusencias[i];
                    diasConAusencias[i] = diasConAusencias[j];
                    diasConAusencias[j] = temp;
                }
            }
        }

        // Mostrar días ordenados
        System.out.println("\nDías ordenados por número de ausencias (de menor a mayor):");
        for (int i = 0; i < DIAS_SEMANA; i++) {
            System.out.println("Día " + diasConAusencias[i][0] + ": " + diasConAusencias[i][1] + " ausencias");
        }

        // Identificar los días con mayor número de ausencias
        int maxAusencias = diasConAusencias[DIAS_SEMANA - 1][1];
        System.out.print("\nDía(s) con mayor número de ausencias: ");
        boolean primero = true;
        for (int i = 0; i < DIAS_SEMANA; i++) {
            if (diasConAusencias[i][1] == maxAusencias) {
                if (!primero) {
                    System.out.print(", ");
                }
                System.out.print("Día " + diasConAusencias[i][0]);
                primero = false;
            }
        }
        System.out.println(" (" + maxAusencias + " ausencias)");
    }
}