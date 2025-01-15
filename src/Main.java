import java.util.ArrayList;

public class Main {
    private volatile static Intersection intersection;
    private volatile static JObjectsHandler jObjectsHandler;
    private volatile static ArrayList<Car> carsObjectsList = new ArrayList<>();
    private static int numberOfCars;
    private static volatile boolean safeDelete = false;

    public static JObjectsHandler getjObjectsHandler() {
        return jObjectsHandler;
    }

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

    public static void deleteCar(Car car) {
        car.stopLoop();
        while (safeDelete){
            boolean found = safeDelete;
            System.out.println("Car #" + car.getCarId() + " waiting.");
            if (!safeDelete){
                break;
            }
            int i = 1/2;
        }
        safeDelete = true;

        int indexInCarsObjectsList = findIndexInCarsObjectsList(car);
        if (indexInCarsObjectsList == -1) {
            throw new IllegalStateException("Car did not found index of itself");
        }

        if (carsObjectsList.get(indexInCarsObjectsList).getCarId() != car.getCarId()){
            throw new IllegalStateException("issue 2");
        }
        carsObjectsList.remove(indexInCarsObjectsList);

        safeDelete = false;
        car.interrupt();
    }

    private static int findIndexInCarsObjectsList(Car car) {
        for (int i = 0; i < carsObjectsList.size(); i++) {
            if (carsObjectsList.get(i) == car) {
                return i;
            }
        }

        return -1;
    }
}