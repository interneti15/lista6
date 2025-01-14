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
    private int startingEntranceID;
    private int endEntranceID;

    private STATUS status = null;


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
        this.startingEntranceID = new Random().nextInt(intersection.getEntrances().size());
        this.endEntranceID = (startingEntranceID + new Random().nextInt(intersection.getEntrances().size() - 1)) % intersection.getEntrances().size();

        int laneID = findCorrectPathID(intersection, startingEntranceID, endEntranceID);
        if (laneID == -1){
            findNewPath();
        }

        status = STATUS.WAITING;
    }

    private static int findCorrectPathID(Intersection intersection, int startingEntranceID, int endEntranceID) {
        ArrayList<Entrance.Lane> availableLanes = intersection.getEntrances().get(startingEntranceID).getLanes();
        for (Entrance.Lane lane : availableLanes) {
            for (Entrance.Path path : lane.getPaths()) {
                if (path.getEndEntranceID() == endEntranceID) {
                    return lane.getId();
                }
            }
        }
        return -1;
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