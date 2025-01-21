import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class Entrance {
    private static int ROAD_LANES = 3;
    private final Point start;
    private final Point end;
    private final int id;
    private final int numberOfEntrances;
    private ArrayList<Lane> lanes;
    private double degreeFacingMiddle;
    private ArrayList<Point> entrancePoints;
    private final Intersection intersection;
    private ArrayList<Point> exitPoints;
    private final ArrayList<ArrayList<Point>> exitPathsPoints = new ArrayList<>();
    private final ArrayList<ArrayList<Integer>> exitPathsPointsOccupied = new ArrayList<>();

    Entrance(Point start, Point end, int id, int numberOfEntrances, Intersection intersection) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.numberOfEntrances = numberOfEntrances;
        this.intersection = intersection;

        checkValidity();
        calculateDegreeFacingMiddle();
        createSpecialPoints();
        createExitPathPoints();
        createLanes();


        //System.out.println("Entrance: " + start + " End: " + end);
        //System.out.println(degreeFacingMiddle);
    }

    public static void setRoadLanes(int roadLanes) {
        ROAD_LANES = roadLanes;
    }

    public ArrayList<Point> getExitPoints() {
        return exitPoints;
    }

    public int getId() {
        return id;
    }


    private void createExitPathPoints() {
        exitPathsPoints.clear();
        exitPathsPointsOccupied.clear();

        for (Point exitPoint : exitPoints) {
            ArrayList<Point> temporaryExitPath = new ArrayList<>();
            double SCALE = 2;
            Point step = new Point(Math.cos(this.degreeFacingMiddle + Math.PI), (-1) * Math.sin(this.degreeFacingMiddle + Math.PI));
            Point currentPoint = new Point(exitPoint);
            while (currentPoint.getX() <= this.intersection.getMainApplicationWindow().getWindowSize() && currentPoint.getY() <= this.intersection.getMainApplicationWindow().getWindowSize() && currentPoint.getX() >= 0 && currentPoint.getY() >= 0) {
                temporaryExitPath.add(new Point(currentPoint));
                currentPoint = new Point(currentPoint.getX() + step.getX() * SCALE, currentPoint.getY() + step.getY() * SCALE);
            }
            this.exitPathsPoints.add(temporaryExitPath);
            this.exitPathsPointsOccupied.add(new ArrayList<>(Collections.nCopies(temporaryExitPath.size(), (-1))));
        }
    }

    public ArrayList<ArrayList<Point>> getExitPathsPoints() {
        return exitPathsPoints;
    }

    private void checkValidity() {
        if (this.numberOfEntrances - 1 <= ROAD_LANES) {
            ROAD_LANES = this.numberOfEntrances - 1;
        }
    }

    private void calculateDegreeFacingMiddle() {
        double dx = (-1) * (end.getX() - start.getX());
        double dy = (end.getY() - start.getY());
        degreeFacingMiddle = (Math.atan2(dy, dx)) + Math.PI / 2;
    }

    private void createSpecialPoints() {

        final int PADDING = 1;
        ArrayList<Point> toReturn = new ArrayList<>();
        int totalPoints = 2 * ROAD_LANES + 3 * PADDING;

        double deltaX = (end.getX() - start.getX()) / (totalPoints - 1);
        double deltaY = (end.getY() - start.getY()) / (totalPoints - 1);

        for (int i = 0; i < totalPoints; i++) {
            double x = start.getX() + i * deltaX;
            double y = start.getY() + i * deltaY;
            toReturn.add(new Point(x, y));
        }

        //this.entrancePoints = (ArrayList<Point>) toReturn.subList(PADDING, PADDING + ROAD_LANES);
        this.entrancePoints = new ArrayList<>();
        for (int i = PADDING; i < PADDING + ROAD_LANES; i++) {
            entrancePoints.add(toReturn.get(i));
        }
        this.exitPoints = new ArrayList<>();
        for (int i = PADDING + ROAD_LANES + PADDING; i < PADDING + ROAD_LANES + PADDING + ROAD_LANES; i++) {
            exitPoints.add(toReturn.get(i));
        }

    }

    private void createLanes() {

        this.lanes = new ArrayList<>(ROAD_LANES);
        int pathsPerLane = (this.numberOfEntrances - 1) / ROAD_LANES;
        int extraPaths = (this.numberOfEntrances - 1) % ROAD_LANES;
        int current = (this.id - 1 + this.numberOfEntrances) % this.numberOfEntrances;
        for (int i = 0; i < ROAD_LANES; i++) {
            int lanePaths = pathsPerLane + (i < extraPaths ? 1 : 0);

            ArrayList<Integer> exitIDs = calculateExitIDs(current, lanePaths, this.numberOfEntrances);
            current = (exitIDs.getLast() - 1 + this.numberOfEntrances) % this.numberOfEntrances;
            this.lanes.add(new Lane(i, entrancePoints.get(i), lanePaths, degreeFacingMiddle, this, exitIDs));
            //System.out.println("Debug - i: " + i + ", entrancePoint: " + (entrancePoints != null && entrancePoints.size() > i ? entrancePoints.get(i) : "Invalid index") + ", lanePaths: " + lanePaths + ", degreeFacingMiddle: " + degreeFacingMiddle);
            //System.out.println("Debug - i: " + i + ", entrancePoint: " + (exitPoints != null && exitPoints.size() > i ? exitPoints.get(i) : "Invalid index"));
        }
    }
    private static ArrayList<Integer> calculateExitIDs(int current, int numberOfUsedPaths, int totalExits){
        ArrayList<Integer> toReturn = new ArrayList<>();

        for (int i = 0; i < numberOfUsedPaths; i++) {

            toReturn.add(current);
            current = (current - 1 + totalExits) % totalExits;
        }

        return toReturn;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public void buildLanes() {
        for (Lane lane : lanes) {
            System.out.println("        Building lane#" + lane.id);
            lane.calculatePaths();
        }
    }

    public ArrayList<Lane> getLanes() {
        return lanes;
    }

    public ArrayList<Point> getEntrancePoints() {
        return entrancePoints;
    }

    public static class EntranceHandler extends JPanel {
        EntranceHandler(MainApplicationWindow mainApplicationWindow) {
            this.setOpaque(true);
            mainApplicationWindow.add(this);
        }
    }

    public static class Lane {
        private final int id;
        private final Point position;
        private final int numberOfPaths;
        private final double directionAngle;
        private final Entrance entrance;
        private final ArrayList<Car> waitingCars = new ArrayList<Car>();
        private final ArrayList<Path> paths = new ArrayList<>();
        private final ArrayList<Point> queuePoints = new ArrayList<>();
        private final ArrayList<Integer> queuePointsOccupied = new ArrayList<>();
        private boolean isGreenLight = false;
        private final ArrayList<Integer> exitIDs;

        public Lane(int id, Point position, int numberOfPaths, double directionAngle, Entrance entrance, ArrayList<Integer> exitIDs) {
            this.id = id;
            this.position = position;
            this.numberOfPaths = numberOfPaths;
            this.directionAngle = directionAngle;
            this.entrance = entrance;
            this.exitIDs = exitIDs;
            buildQueuePoints();
            System.out.println("1");
        }

        public ArrayList<Integer> getQueuePointsOccupied() {
            return queuePointsOccupied;
        }

        public ArrayList<Car> getWaitingCars() {
            return waitingCars;
        }

        private void buildQueuePoints() {
            double SCALE = 2;
            Point step = new Point(Math.cos(this.directionAngle + Math.PI), (-1)*Math.sin(this.directionAngle + Math.PI));
            Point currentPoint = new Point(this.position);
            while (currentPoint.getX() <= this.entrance.intersection.getMainApplicationWindow().getWindowSize() && currentPoint.getY() <= this.entrance.intersection.getMainApplicationWindow().getWindowSize() && currentPoint.getX() >= 0 && currentPoint.getY() >= 0) {
                queuePoints.add(new Point(currentPoint));
                queuePointsOccupied.add(-1);
                currentPoint = new Point(currentPoint.getX() + step.getX() * SCALE, currentPoint.getY() + step.getY() * SCALE);
            }
            Collections.reverse(queuePoints);
        }

        public Entrance getEntrance() {
            return entrance;
        }

        public double getHottness() {
            double sum = 0;
            for (Car car : waitingCars) {
                sum += car.waitingTime;
            }
            return sum;
        }

        public void calculatePaths() {
            for (int i = 0; i < numberOfPaths; i++) {
                int connectedToEntranceID = exitIDs.get(i);
                Entrance endEntrance = this.entrance.intersection.getEntrances().get(connectedToEntranceID);
                Point endPoint = endEntrance.exitPoints.get(ROAD_LANES - 1 - this.id);

                double distance = this.position.distanceTo(endPoint);
                double offset = (distance * Math.sqrt(2) / (entrance.numberOfEntrances - 1)) / 1.2;
                double SAMPLING_RATIO = 70;
                int SAMPLING_RATE = (int) (((SAMPLING_RATIO - 200) / (-365.0)) * distance + 200 - (450 * (SAMPLING_RATIO - 200) / -365.0));

                System.out.println("            Bulding path #" + this.getId() + ", from: " + this.entrance.id + ", to: " + connectedToEntranceID + ", exit id: " + (ROAD_LANES - 1 - this.id));
                ArrayList<Point> curvePoints = CurveGenerator.generateCurveAndPoints(this.position.getX(), this.position.getY(), this.directionAngle, endPoint.getX(), endPoint.getY(), endEntrance.degreeFacingMiddle, SAMPLING_RATE, offset);
                this.paths.add(new Path(curvePoints, this, endEntrance, (ROAD_LANES - 1 - this.id)));
            }
        }

        public void setGreenLightForAllPaths(boolean lightForAllPaths) {
            for (Path path : paths) {
                path.setGreenLight(lightForAllPaths);
            }
        }

        public ArrayList<Path> getPaths() {
            return paths;
        }

        public synchronized void addCarToQueue(Car car) {
            this.waitingCars.add(car);
        }

        public synchronized void removeCarFromQueue(Car car) {
            this.waitingCars.remove(car);
        }

        public ArrayList<Point> getQueuePoints() {
            return queuePoints;
        }

        public int getId() {
            return id;
        }
        public boolean isGreenLight() {
            return isGreenLight;
        }

        public void setGreenLight(boolean greenLight) {
            isGreenLight = greenLight;
        }

        public ArrayList<Point.Line> pathsToLines(){
            ArrayList<Point.Line> lines = new ArrayList<>();
            for (Path path : this.paths){
                lines.add(new Point.Line(path.startLane.position, path.endEntrance.exitPoints.get(path.endExitID)));
            }
            return lines;
        }

        public Point getPosition() {
            return position;
        }
    }

    public static class Path {
        private final ArrayList<Point> intersectionPath;
        private final Entrance endEntrance;
        private final Lane startLane;
        private final int endExitID;
        private final ArrayList<Car> carsUsingThisPath = new ArrayList<>();
        private boolean isGreenLight = false;

        public Path(ArrayList<Point> intersectionPath, Lane startLane, Entrance endEntrance, int endExitID) {
            this.intersectionPath = intersectionPath;
            this.endEntrance = endEntrance;
            this.endExitID = endExitID;
            this.startLane = startLane;
        }

        public ArrayList<Car> getCarsUsingThisPath() {
            return carsUsingThisPath;
        }

        public Lane getStartLane() {
            return startLane;
        }

        public ArrayList<Point> getIntersectionPath() {
            return intersectionPath;
        }

        public Entrance getEndEntrance() {
            return endEntrance;
        }

        public int getEndExitID() {
            return endExitID;
        }

        public synchronized void addCarToPath(Car car) {
            this.carsUsingThisPath.add(car);
        }

        public Point.Line toLine(){
            return new Point.Line(this.getStartLane().getPosition(), this.getEndEntrance().getExitPoints().get(this.getEndExitID()));
        }

        public synchronized void removeCarFromPath(Car car) {

            this.carsUsingThisPath.remove(car);

        }

        public double getHottness(){
            double sum = 0;
            for (Car car : carsUsingThisPath){
                sum += car.waitingTime;
            }
            return sum;
        }

        public boolean isGreenLight() {
            return isGreenLight;
        }

        public void setGreenLight(boolean greenLight) {
            isGreenLight = greenLight;
        }
    }
}
