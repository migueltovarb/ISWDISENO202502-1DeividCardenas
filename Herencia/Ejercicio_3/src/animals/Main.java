package animals;

public class Main {
    public static void main(String[] args) {
        Animal a = new Animal("Generic");
        Mammal m = new Mammal("Manny");
        Cat c = new Cat("Whiskers");
        Dog d1 = new Dog("Rex");
        Dog d2 = new Dog("Buddy");

        System.out.println(a.toString());
        System.out.println(m.toString());
        System.out.println(c.toString());
        System.out.println(d1.toString());
        System.out.println(d2.toString());

        c.greets();      
        d1.greets();     
        d1.greets(d2);  
    }
}
