import java.util.ArrayList;
import java.util.Random;

public class Car extends Thread {
    private static int number = 0;
    private int id;
    Point position = new Point(300, 300);
    int waitingTime = 0;
    Intersection intersection;
    MainApplicationWindow mainApplicationWindow;
    private STATUS status = null;
    private Entrance startingEntrance;
    private Entrance.Lane choosenLane;
    private Entrance.Path choosenPath;


    Car(Intersection intersection, MainApplicationWindow mainApplicationWindow) {
        this.id = number;
        number++;
        this.intersection = intersection;
        this.mainApplicationWindow = mainApplicationWindow;
    }

    @Override
    public void run() {
        while (true) {
            if (choosenPath == null || status == null) {
                findNewPath();
            }

            switch (status) {
                case MOVING_TO_ENTRANCE -> handleCarMovingToEntranceState();
                case WAITING -> handleCarWaitingOnEntrance();
                case MOVING -> handleCarMovingState();
                case ARRIVING -> handleCarArrivingState();
            }
        }
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
            position.setX((int)(point.getX() - 7));
            position.setY((int)(point.getY() - 7));

            try {
                Thread.sleep(100);
                Main.getIntersection().getIntersectionJPanelHandler().repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        status = STATUS.MOVING;
    }

    private void handleCarWaitingOnEntrance() {
        waitingTime++;
    }

    private void handleCarMovingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getIntersectionPath();

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX((int)(point.getX() - 7));
            position.setY((int)(point.getY() - 7));

            try {
                Thread.sleep(100);
                Main.getIntersection().getIntersectionJPanelHandler().repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        status = STATUS.ARRIVING;
    }

    private void handleCarArrivingState() {
        // GET PATH OF END INTERSECTION POINTS

        // DELETE CAR
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

        // 7 is half of 14 which is the size of the car (for make it centered)
        position = new Point((int)(startPointX - 7), (int)(startPointY - 7));

        status = STATUS.MOVING_TO_ENTRANCE;
    }

    private void findCorrectPath(Intersection intersection, int startingEntranceID, int endEntranceID) {
        ArrayList<Entrance.Lane> availableLanes = intersection.getEntrances().get(startingEntranceID).getLanes();
        for (Entrance.Lane lane : availableLanes) {
            for (Entrance.Path path : lane.getPaths()) {
                if (path.getEndEntrance().getId() == endEntranceID) {
                    this.choosenLane = lane;
                    this.choosenPath = path;
                    return;
                }
            }
        }
        findNewPath();
    }

    enum STATUS {
        MOVING_TO_ENTRANCE,
        WAITING,
        MOVING,
        ARRIVING
    }
}