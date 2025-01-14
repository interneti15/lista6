import java.util.ArrayList;

public class Main {
    private volatile static Intersection intersection;
    private volatile static JObjectsHandler jObjectsHandler;
    private volatile static ArrayList<Car> carsObjectsList = new ArrayList<>();

    public static JObjectsHandler getjObjectsHandler() {
        return jObjectsHandler;
    }

    public static void main(String[] args) {
        jObjectsHandler = new JObjectsHandler();

        for (int i = 0; i < 1; i++) {
            Car car = new Car(getIntersection(), jObjectsHandler.getApplicationMainJFrame());
            carsObjectsList.add(car);
        }
        for (Car car : carsObjectsList) {
            car.start();
        }
    }

    public static Intersection getIntersection() {
        return intersection;
    }

    public static void setIntersection(Intersection intersection) {
        Main.intersection = intersection;
    }
}