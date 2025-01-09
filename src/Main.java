public class Main {
    private static Intersection intersection;
    private static MainApplicationWindow mainApplicationWindow;

    public static void main(String[] args) {
        Main.mainApplicationWindow = new MainApplicationWindow("Cars Simulation");
        Main.intersection = new Intersection(4, mainApplicationWindow);
    }
}