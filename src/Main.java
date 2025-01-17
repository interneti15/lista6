import java.util.ArrayList;

public class Main {
    private volatile static Intersection intersection;
    private volatile static JObjectsHandler jObjectsHandler;
    private static final ArrayList<Car> carsObjectsList = new ArrayList<>();
    private static int numberOfCars;

    public static void main(String[] args) {
        jObjectsHandler = new JObjectsHandler();

        for (int i = 0; i < numberOfCars; i++) {
            addNewCar();
        }

        intersection.start();
    }

    public static void setNumberOfCars(int numberOfCars) {
        Main.numberOfCars = numberOfCars;
    }

    public static Intersection getIntersection() {
        return intersection;
    }

    public static void setIntersection(Intersection intersection) {
        Main.intersection = intersection;
    }

    public static void addNewCar() {
        Car car = new Car(intersection, jObjectsHandler.getApplicationMainJFrame());
        carsObjectsList.add(car);
        car.start();
    }

    public static ArrayList<Car> getCarsObjectsList() {
        return carsObjectsList;
    }

}