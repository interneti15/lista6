public class Main {
    private volatile static Intersection intersection;
    private volatile static JObjectsHandler jObjectsHandler;

    public static JObjectsHandler getjObjectsHandler() {
        return jObjectsHandler;
    }

    public static void main(String[] args) {
        jObjectsHandler = new JObjectsHandler();
    }

    public static void setIntersection(Intersection intersection) {
        Main.intersection = intersection;
    }

    public static Intersection getIntersection() {
        return intersection;
    }
}