package figuras;

public class Main {
    public static void main(String[] args) {
        Circle circle = new Circle(2.5, "blue", false);
        Rectangle rectangle = new Rectangle(3.0, 4.5, "green", true);
        Square square = new Square(5.0, "yellow", true);

        System.out.println(circle.toString());
        System.out.println(rectangle.toString());
        System.out.println(square.toString());
    }
}
