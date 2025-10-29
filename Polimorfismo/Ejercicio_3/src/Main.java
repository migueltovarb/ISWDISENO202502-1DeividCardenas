public class Main {
    public static void main(String[] args) {

        Secretario sec1 = new Secretario("Ana", "García", "111A", "Calle Falsa 1", "555-1111", 30000, "Despacho 101", "555-1112");
        Vendedor vend1 = new Vendedor("Carlos", "Ruiz", "222B", "Av. Siempre 2", "555-2222", 35000, "555-2223", "Zona Norte", 0.05);
        JefeDeZona jefe1 = new JefeDeZona("Laura", "Pérez", "333C", "Plaza Mayor 3", "555-3333", 50000, "Oficina Principal");

        vend1.cambiarCoche("BCF-123", "Ford", "Focus");
        jefe1.cambiarSecretario(sec1);
        jefe1.darDeAltaVendedor(vend1);
        vend1.cambiarSupervisor(jefe1);
        sec1.cambiarSupervisor(jefe1);
        
        Empleado[] plantilla = { sec1, vend1, jefe1 };

        System.out.println("======= SALARIOS INICIALES =======");
        for (Empleado emp : plantilla) {
            emp.imprimir();
            System.out.println("---------------------------------");
        }

        System.out.println("\n======= APLICANDO INCREMENTOS DE SALARIO... =======");
        for (Empleado emp : plantilla) {
            emp.incrementarSalario();
        }

        System.out.println("\n======= SALARIOS ACTUALIZADOS =======");
        System.out.println("(Se debe ver +5% para Ana, +10% para Carlos, +20% para Laura)");

        for (Empleado emp : plantilla) {
            System.out.println("---------------------------------");
            System.out.println("Empleado: " + emp.nombre);
            System.out.println("Nuevo Salario: $" + String.format("%.2f", emp.getSalario()));
        }
    }
}