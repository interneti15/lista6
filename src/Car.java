import java.util.ArrayList;
import java.util.Random;

public class Car extends Thread {
    private static int number = 0;
    private final int id;
    private final int CAR_MOVE_DELAY = 50;
    Point position = new Point(300, 300);
    double waitingTime = 5;
    Intersection intersection;
    MainApplicationWindow mainApplicationWindow;
    private STATUS status = STATUS.ARRIVING;
    private Entrance startingEntrance;
    private Entrance.Lane choosenLane;
    private Entrance.Path choosenPath;
    private boolean isAlive = true;

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
        ArrayList<Integer> intersectionPointsOccupied = choosenPath.getStartLane().getQueuePointsOccupied();

        while (intersectionPointsOccupied.getFirst() != -1){
            sleepWithRepaint(CAR_MOVE_DELAY);
        }
        intersectionPointsOccupied.set(0,this.id);

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
                waitingTime += 0.000001;
                i--;
            }

            /*for (Integer integer : intersectionPointsOccupied){
                System.out.print(String.valueOf(integer+1));
            }
            System.out.println();*/

            sleepWithRepaint(CAR_MOVE_DELAY);
        }

        /*for (Integer integer : intersectionPointsOccupied){
            System.out.print(String.valueOf(integer+1));
        }
        System.out.println();*/
        status = STATUS.WAITING;
    }

    private void occupySpace(ArrayList<Integer> intersectionPointsOccupied, int index) {
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
        boolean test = choosenLane.isGreenLight();
        if (choosenLane.isGreenLight()) {
            status = STATUS.MOVING;
            for (int i = intersectionPointsOccupied.size() - 6; i < intersectionPointsOccupied.size(); i++) {
                intersectionPointsOccupied.set(i, -1);
            }
            return;
        }

        waitingTime += 0.000001;
    }

    private void handleCarMovingState() {
        ArrayList<Point> intersectionPoints = choosenPath.getIntersectionPath();

        for (int i = 0; i < intersectionPoints.size() - 1; i++) {
            Point point = intersectionPoints.get(i + 1);
            position.setX(point.getX());
            position.setY(point.getY());

            sleepWithRepaint(CAR_MOVE_DELAY);
        }

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

        Main.deleteCar(this);
        Main.addNewCar();
    }

    private void findNewPath() {
        int startingEntranceID = new Random().nextInt(intersection.getEntrances().size());
        int endEntranceID = (startingEntranceID + new Random().nextInt(intersection.getEntrances().size() - 1) + 1) % intersection.getEntrances().size();

        //System.out.println("start: " + startingEntranceID + " end: " + endEntranceID);

        startingEntrance = intersection.getEntrances().get(startingEntranceID);

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
                    this.choosenLane = lane;
                    this.choosenPath = path;
                    return;
                }
            }
        }
        //findCorrectPath(intersection, startingEntranceID, endEntranceID);
        throw new IllegalStateException("IllegalPath");
    }

    private void sleepWithRepaint(int time) {
        try {
            Thread.sleep(time);

            intersection.getIntersectionJPanelHandler().repaint();
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