import java.util.ArrayList;
import java.util.Random;

public class Car extends Thread {
    private static int number = 0;
    private int id;
    private final int CAR_MOVE_DELAY = 50;
    Point position = new Point(300, 300);
    double waitingTime = 0;
    Intersection intersection;
    MainApplicationWindow mainApplicationWindow;
    private STATUS status = STATUS.ARRIVING;
    //private Entrance startingEntrance;
    private Entrance.Lane choosenLane;
    private Entrance.Path choosenPath;
    private final boolean isAlive = true;

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
        waitingTime = 0;
        ArrayList<Point> intersectionPoints = choosenPath.getStartLane().getQueuePoints();
        ArrayList<Integer> intersectionPointsOccupied = choosenPath.getStartLane().getQueuePointsOccupied();

        while (intersectionPointsOccupied.getFirst() != -1){
            sleepWithRepaint(CAR_MOVE_DELAY);
        }
        intersectionPointsOccupied.set(0,this.id);

        long startedWaitingOnTime = System.currentTimeMillis();

        for (int i = 0; i < intersectionPoints.size(); i++) {
            boolean available = checkForSpaceAvailability(intersectionPointsOccupied, i);

            if (available) {
                Point point = intersectionPoints.get(i);
                position.setX(point.getX());
                position.setY(point.getY());
                occupySpace(intersectionPointsOccupied,i);
                if (i - 6 >= 0){
                    intersectionPointsOccupied.set(i - 6, -1);
                }
            } else {
                waitingTime += System.currentTimeMillis() - startedWaitingOnTime;
                startedWaitingOnTime = System.currentTimeMillis(); // Reset only after incrementing

                i--;
            }

            sleepWithRepaint(CAR_MOVE_DELAY);
        }

        status = STATUS.WAITING;
    }

    private synchronized void occupySpace(ArrayList<Integer> intersectionPointsOccupied, int index) {
        for (int i = index - 5; i < intersectionPointsOccupied.size() && i < index + 5; i++) {
            if (i < 0){
                continue;
            }

            intersectionPointsOccupied.set(i, this.id);

        }
    }

    private boolean checkForSpaceAvailability(ArrayList<Integer> intersectionPointsOccupied, int index) {
        for (int i = index; i < intersectionPointsOccupied.size() && i < index + 10; i++) {
            if (intersectionPointsOccupied.get(i) != -1 && intersectionPointsOccupied.get(i) != this.id) {
                return false;
            }
        }
        return true;
    }

    private void handleCarWaitingOnEntrance() {
        ArrayList<Integer> intersectionPointsOccupied = choosenPath.getStartLane().getQueuePointsOccupied();
        long startedWaitingOnTime = System.currentTimeMillis();
        while (true)
        {
            if (choosenLane.isGreenLight() || choosenPath.isGreenLight()) {
                status = STATUS.MOVING;
                for (int i = intersectionPointsOccupied.size() - 6; i < intersectionPointsOccupied.size(); i++) {
                    intersectionPointsOccupied.set(i, -1);
                }
                return;
            }

            waitingTime += System.currentTimeMillis() - startedWaitingOnTime;
            startedWaitingOnTime = System.currentTimeMillis(); // Reset only after incrementing
            sleepWithRepaint(CAR_MOVE_DELAY);
        }
    }

    private void handleCarMovingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getIntersectionPath();
        choosenPath.getStartLane().removeCarFromQueue(this);
        choosenPath.removeCarFromPath(this);
        waitingTime = 0;

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            sleepWithRepaint(CAR_MOVE_DELAY);
        }

        intersection.increaseCarsThatExitedIntersection();
        status = STATUS.ARRIVING;
    }

    private void handleCarArrivingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getEndEntrance().getExitPathsPoints().get(choosenPath.getEndExitID());

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            sleepWithRepaint(CAR_MOVE_DELAY);
        }

        this.regenerateID();
        status = null;

    }

    private void findNewPath() {
        int startingEntranceID = new Random().nextInt(intersection.getEntrances().size());
        int endEntranceID = (startingEntranceID + new Random().nextInt(intersection.getEntrances().size() - 1) + 1) % intersection.getEntrances().size();

        findCorrectPath(intersection, startingEntranceID, endEntranceID);
        //System.out.println("Car " + id + " at entrance: " + choosenPath.getStartEntrance().getId() + " in lane: " + choosenPath.getStartLane().getId() + " destination at: " + choosenPath.getEndEntrance().getId() + " exit number: " + choosenPath.getEndExitID());

        Point entranceStartPoint = choosenPath.getStartLane().getQueuePoints().getFirst();
        double startPointX = entranceStartPoint.getX();
        double startPointY = entranceStartPoint.getY();

        position = new Point((int) (startPointX), (int) (startPointY));
        status = STATUS.MOVING_TO_ENTRANCE;
    }

    private void findCorrectPath(Intersection intersection, int startingEntranceID, int endEntranceID) {
        ArrayList<Entrance.Lane> availableLanes = intersection.getEntrances().get(startingEntranceID).getLanes();
        for (Entrance.Lane lane : availableLanes) {
            for (Entrance.Path path : lane.getPaths()) {
                if (path.getEndEntrance().getId() == endEntranceID) {
                    lane.addCarToQueue(this);
                    path.addCarToPath(this);
                    this.choosenLane = lane;
                    this.choosenPath = path;
                    return;
                }
            }
        }
        throw new IllegalStateException("IllegalPath");
    }

    private void sleepWithRepaint(int time) {
        try {
            Thread.sleep(time);

            intersection.getIntersectionJPanelHandler().repaint();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
        intersection.getIntersectionJPanelHandler().repaint();
    }

    enum STATUS {
        MOVING_TO_ENTRANCE,
        WAITING,
        MOVING,
        ARRIVING
    }

    private void regenerateID(){
        this.id = number;
        number++;
    }
}