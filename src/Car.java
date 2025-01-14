import java.util.ArrayList;
import java.util.Random;

public class Car extends Thread {
    private static int number = 0;
    private final int id;
    Point position = new Point(300, 300);
    int waitingTime = 1;
    Intersection intersection;
    MainApplicationWindow mainApplicationWindow;
    private STATUS status = STATUS.ARRIVING;
    private Entrance startingEntrance;
    private Entrance.Lane choosenLane;
    private Entrance.Path choosenPath;
    private boolean isAlive = true;
    private final int carMoveDelay = 50;

    Car(Intersection intersection, MainApplicationWindow mainApplicationWindow) {
        this.id = number;
        number++;
        this.intersection = intersection;
        this.mainApplicationWindow = mainApplicationWindow;
    }

    @Override
    public void run() {
        while (isAlive) {
            if (choosenPath == null || status == null) {
                findNewPath();
            }

            //choosenPath.getStartLane().getQueuePoints();
            //choosenPath.getEndEntrance().getExitPathsPoints().get(choosenPath.getEndExitID())

            switch (status) {
                case MOVING_TO_ENTRANCE -> handleCarMovingToEntranceState();
                case WAITING -> handleCarWaitingOnEntrance();
                case MOVING -> handleCarMovingState();
                case ARRIVING -> handleCarArrivingState();
            }
        }
    }

    public void stopLoop() {
        isAlive = false;
    }

    public int getCarId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    private void handleCarMovingToEntranceState() {
        ArrayList<Point> intersectionPoints = choosenPath.getStartLane().getQueuePoints();

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            SleepWithRepaint(carMoveDelay);
        }

        status = STATUS.WAITING;
    }

    private void handleCarWaitingOnEntrance() {
        if (!choosenLane.isGreenLight()) {
            status = STATUS.MOVING;
            return;
        }

        waitingTime++;
    }

    private void handleCarMovingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getIntersectionPath();

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            SleepWithRepaint(carMoveDelay);
        }

        status = STATUS.ARRIVING;
    }

    private void handleCarArrivingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getEndEntrance().getExitPathsPoints().get(choosenPath.getEndExitID());

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            SleepWithRepaint(carMoveDelay);
        }

        Main.deleteCar(this);
    }

    private void findNewPath() {
        int startingEntranceID = new Random().nextInt(intersection.getEntrances().size());
        int endEntranceID = (startingEntranceID + new Random().nextInt(intersection.getEntrances().size() - 1)) % intersection.getEntrances().size();

        System.out.println("start: " + startingEntranceID);
        System.out.println("end: " + endEntranceID);

        startingEntrance = intersection.getEntrances().get(startingEntranceID);
        
        findCorrectPath(intersection, startingEntranceID, endEntranceID);
        System.out.println("Car " + id + " at entrance: " + choosenPath.getStartEntrance().getId() + " in lane: " + choosenPath.getStartLane().getId() + " destination at: " + choosenPath.getEndEntrance().getId() + " exit number: " + choosenPath.getEndExitID());

        Point entranceStartPoint = choosenPath.getStartLane().getQueuePoints().getFirst();
        double startPointX = entranceStartPoint.getX();
        double startPointY = entranceStartPoint.getY();

        position = new Point((int)(startPointX), (int)(startPointY));
        status = STATUS.MOVING_TO_ENTRANCE;
    }

    private void findCorrectPath(Intersection intersection, int startingEntranceID, int endEntranceID) {
        ArrayList<Entrance.Lane> availableLanes = intersection.getEntrances().get(startingEntranceID).getLanes();
        for (Entrance.Lane lane : availableLanes) {
            for (Entrance.Path path : lane.getPaths()) {
                if (path.getEndEntrance().getId() == endEntranceID) {
                    lane.addCarToQueue(this);
                    this.choosenLane = lane;
                    this.choosenPath = path;
                    return;
                }
            }
        }
        findNewPath();
    }

    private void SleepWithRepaint(int time) {
        try {
            Thread.sleep(time);

            Main.getIntersection().getIntersectionJPanelHandler().repaint();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
    }

    enum STATUS {
        MOVING_TO_ENTRANCE,
        WAITING,
        MOVING,
        ARRIVING
    }
}