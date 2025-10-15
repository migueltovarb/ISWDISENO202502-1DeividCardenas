package institucion;

public class Main {
    public static void main(String[] args) {
        Student student = new Student("Ana", "Calle 123", "Ingeniería", 3, 4500.00);
        Staff staff = new Staff("Luis", "Av. Siempre Viva", "Colegio Nacional", 3200.50);

        System.out.println(student.toString());
        System.out.println(staff.toString());
    }
}
