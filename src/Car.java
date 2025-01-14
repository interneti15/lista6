import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Car extends Thread {
    private static int number = 0;
    private int id;
    Point position = new Point(300, 300);
    int waitingTime = 0;
    Entrance.Path currentPath = null;
    Intersection intersection;
    MainApplicationWindow mainApplicationWindow;
    CarHandler carHandler;
    private STATUS status = null;
    private Entrance startingEntrance;
    private Entrance.Lane choosenLane;
    private Entrance.Path choosenPath;


    Car(Intersection intersection, MainApplicationWindow mainApplicationWindow) {
        this.id = number;
        number++;
        this.intersection = intersection;
        this.mainApplicationWindow = mainApplicationWindow;
        this.carHandler = new CarHandler(this);
    }

    @Override
    public void run() {
        while (true) {
            if (currentPath == null || status == null) {
                findNewPath();
            }

            switch (status) {
                case WAITING:
                    waitingTime++;
                    break;
                case MOVING:
                    waitingTime = 0;
                    break;
                case ARRIVING:
                    break;
            }
        }
    }

    private void findNewPath() {
        int startingEntranceID = new Random().nextInt(intersection.getEntrances().size());
        int endEntranceID = (startingEntranceID + new Random().nextInt(intersection.getEntrances().size() - 1)) % intersection.getEntrances().size();

        startingEntrance = intersection.getEntrances().get(startingEntranceID);

        findCorrectPath(intersection, startingEntranceID, endEntranceID);

        status = STATUS.WAITING;
    }

    private void findCorrectPath(Intersection intersection, int startingEntranceID, int endEntranceID) {
        ArrayList<Entrance.Lane> availableLanes = intersection.getEntrances().get(startingEntranceID).getLanes();
        for (Entrance.Lane lane : availableLanes) {
            for (Entrance.Path path : lane.getPaths()) {
                if (path.getEndEntranceID() == endEntranceID) {
                    this.choosenLane = lane;
                    this.choosenPath = path;
                }
            }
        }
        findNewPath();
    }

    static class CarHandler extends JPanel {
        Car car;

        CarHandler(Car car) {
            this.car = car;
            this.setOpaque(true);
            car.mainApplicationWindow.add(this);
        }


    }

    enum STATUS {
        WAITING,
        MOVING,
        ARRIVING
    }
}