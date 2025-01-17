import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Comparator;

public class Intersection extends Thread {
    private final int intersectionTime = 5000;
    private ArrayList<Entrance> entrances = new ArrayList<>();
    private IntersectionHandler intersectionJPanelHandler;
    private int entrancesNumber;
    private MainApplicationWindow mainApplicationWindow;
    private int carsThatExitedIntersection = 0;
    private long startTime;
    private final ArrayList<Double> allCurrentAverageWaitingTimes = new ArrayList<>();

    Intersection(int entrances, MainApplicationWindow mainApplicationWindow) {
        this.entrancesNumber = entrances;
        this.mainApplicationWindow = mainApplicationWindow;
        //calculateVertices();
        refreshEntrances();
        this.intersectionJPanelHandler = new IntersectionHandler(entrances, mainApplicationWindow, this);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        waitForFirstCars();

        int iteration = 0;
        while (true) {

            System.out.println("Managing green lights");
            manageGreenLights();
            //setAllToRed();

            System.out.println("Waiting 5000ms");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Setting all lights to red");
            setAllToRed();

            System.out.println("Waiting 6000ms + " + entrancesNumber + " seconds");
            try {
                Thread.sleep(6000 + entrancesNumber * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            iteration++;
        }
    }

    private void waitForFirstCars() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (Main.getCarsObjectsList().size() <= 0) {
        }
    }

    private void setAllToRed() {
        for (Entrance entrance : entrances) {
            for (Entrance.Lane lane : entrance.getLanes()) {
                lane.setGreenLight(false);
            }
        }
    }

    private void manageGreenLights() {
        ArrayList<Entrance.Lane> laneEntries = new ArrayList<>();
        for (Entrance entrance : entrances) {
            for (Entrance.Lane lane : entrance.getLanes()) {
                lane.calculateLaneHotnessIndex();
                laneEntries.add(lane);
            }
        }

        laneEntries.sort(Comparator.comparingDouble(Entrance.Lane::getHottness).reversed());

        ArrayList<Point.Line> lines = new ArrayList<>();
        for (Entrance.Lane lane : laneEntries) {
            if (!MathUtils.doesLinesIntersectFromTwoArrays(lines, lane.pathsToLines())) {
                lines.addAll(lane.pathsToLines());
                lane.setGreenLight(true);
                continue;
            }
            lane.setGreenLight(false);
        }
        //System.out.println();

        intersectionJPanelHandler.repaint();
    }

    public void increaseCarsThatExitedIntersection() {
        carsThatExitedIntersection++;
    }

    public IntersectionHandler getIntersectionJPanelHandler() {
        return intersectionJPanelHandler;
    }

    public ArrayList<Entrance> getEntrances() {
        return entrances;
    }

    private void calculateVertices() {
        double angleStep = 2 * Math.PI / entrancesNumber;
        double angle = (Math.PI / entrancesNumber) + (Math.PI / 2);
        ArrayList<Point> vertices = new ArrayList<>();
        Point middlePoint = mainApplicationWindow.getMiddlePoint();
        double intersectionSize = mainApplicationWindow.getWindowSize() * IntersectionHandler.intersectionSizeRatio / 2;
        //angle = 0;

        for (int i = 0; i < entrancesNumber; i++) {
            double x = middlePoint.getX() + intersectionSize * Math.cos(angle);
            double y = middlePoint.getY() + intersectionSize * Math.sin(angle);
            vertices.add(new Point(x, y));
            angle += angleStep;
        }

        calculateEntrances(vertices);
    }

    private void calculateEntrances(ArrayList<Point> vertices) {
        entrances.clear();
        for (int i = 1; i < vertices.size(); i++) {
            System.out.println("    Creating entrance #" + (i - 1));
            entrances.add(new Entrance(vertices.get(i - 1), vertices.get(i), i - 1, vertices.size(), mainApplicationWindow, this));
        }
        System.out.println("    Creating entrance #" + (vertices.size() - 1));
        entrances.add(new Entrance(vertices.getLast(), vertices.getFirst(), vertices.size() - 1, vertices.size(), mainApplicationWindow, this));


        buildEntrances();
    }

    private void buildEntrances() {
        for (int i = 0; i < entrances.size(); i++) {
            System.out.println("    Building lanes for entrance #" + i);
            entrances.get(i).buildLanes();
        }
    }

    public void refreshEntrances() {
        calculateVertices();
        if (intersectionJPanelHandler != null) {
            intersectionJPanelHandler.repaint();
        }
    }

    public MainApplicationWindow getMainApplicationWindow() {
        return mainApplicationWindow;
    }

    static class IntersectionHandler extends JPanel {
        static final double intersectionSizeRatio = 0.85;
        private Intersection intersection;

        IntersectionHandler(int entrancesNumber, MainApplicationWindow mainApplicationWindow, Intersection intersection) {
            this.intersection = intersection;
            this.setBackground(MyColors.MainForeground);
            this.setBounds(0, 0, mainApplicationWindow.getWindowSize(), mainApplicationWindow.getWindowSize());
            this.setLayout(null);
            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // Print the current mouse position to the console
                    System.out.println("Mouse moved to: (" + e.getX() + ", " + e.getY() + ")");
                }
            });
            mainApplicationWindow.add(this);

            /*this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {

                    int x = e.getX();
                    int y = e.getY();
                    System.out.println("Mouse moved: (" + x + ", " + y + ")");
                }
            });*/

        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (intersection.getEntrances() == null || intersection.getEntrances().size() < 3) {
                return;
            }

            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setColor(Color.BLACK);
            graphics2D.setStroke(new BasicStroke(4));

            String formattedExitRate = getFormattedExitRate();

            /*System.out.println("Car counter: " + intersection.carsThatExitedIntersection);
            System.out.println("Time elapsed: " + ((System.currentTimeMillis() - intersection.startTime)/1000));*/

            graphics2D.setColor(Color.white);
            graphics2D.drawString("Average intersection", 620, 50);
            graphics2D.drawString("capacity", 640, 70);
            graphics2D.drawString((formattedExitRate), 650, 90);
            graphics2D.drawString(" cars per minute", 625, 110);
            graphics2D.setColor(Color.black);

            String averageWaitingTimeString = getTotalCarsAverageWaitingTimeFormatted();
            graphics2D.setColor(Color.white);
            graphics2D.drawString("Average waiting", 620, 130);
            graphics2D.drawString("time", 640, 150);
            graphics2D.drawString(averageWaitingTimeString, 650, 170);
            graphics2D.setColor(Color.black);


            for (Entrance entrance : intersection.getEntrances()) {
                Point start = entrance.getStart();
                Point end = entrance.getEnd();
                if (start != null && end != null) {
                    graphics2D.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
                }
            }
            debugPaths(graphics2D);
        }

        private String getTotalCarsAverageWaitingTimeFormatted() {
            double totalWaitingTime = 0;
            for (Car car : Main.getCarsObjectsList()){
                totalWaitingTime += car.waitingTime;
            }
            double average = totalWaitingTime / (double) Main.getCarsObjectsList().size();
            intersection.allCurrentAverageWaitingTimes.add(average);

            double sumOfAverages = 0;
            for (Double number : intersection.allCurrentAverageWaitingTimes){
                sumOfAverages += number;
            }
            double averageAverage = sumOfAverages / intersection.allCurrentAverageWaitingTimes.size();
            return String.format("%.2f", (averageAverage)/1000);
        }

        private String getFormattedExitRate() {
            double carsExited = (double) intersection.carsThatExitedIntersection;
            double elapsedTime = (double) (System.currentTimeMillis() - intersection.startTime) / 1000;
            double exitRate = carsExited / elapsedTime;
            return String.format("%.2f", exitRate*60);
        }

        private void debugPaths(Graphics2D graphics2D) {
            final int carWidth = 14;
            final int carHeight = 14;

            for (int i = 0; i < this.intersection.entrancesNumber; i++) {
                Entrance entrance = this.intersection.entrances.get(i);

                graphics2D.setColor(Color.YELLOW);
                for (Point point : entrance.getEntrancePoints()) {
                    graphics2D.fillOval(point.getXFloored() - 5, point.getYFloored() - 5, 10, 10);
                }
                graphics2D.setColor(Color.black);

                for (ArrayList<Point> exitPath : entrance.getExitPathsPoints()) {
                    for (Point point : exitPath) {
                        graphics2D.fillOval(point.getXFloored(), point.getYFloored(), 2, 2);
                    }
                }

                for (int j = 0; j < entrance.getLanes().size(); j++) {
                    Entrance.Lane lane = entrance.getLanes().get(j);

                    for (int k = 0; k < lane.getQueuePoints().size(); k++) {
                        Point point = lane.getQueuePoints().get(k);
                        graphics2D.fillOval(point.getXFloored(), point.getYFloored(), 2, 2);
                    }

                    if (lane.isGreenLight()) {
                        graphics2D.setColor(Color.GREEN);
                    } else {
                        graphics2D.setColor(Color.RED);
                    }

                    for (int k = 0; k < lane.getPaths().size(); k++) {
                        Entrance.Path path = lane.getPaths().get(k);
                        for (int l = 1; l < path.getIntersectionPath().size(); l++) {
                            Point prevoiusPoint = path.getIntersectionPath().get(l - 1);
                            Point point = path.getIntersectionPath().get(l);
                            graphics2D.drawLine(point.getXFloored(), point.getYFloored(), prevoiusPoint.getXFloored(), prevoiusPoint.getYFloored());
                        }
                    }
                    graphics2D.setColor(Color.white);
                    graphics2D.drawString(String.valueOf(lane.getId()), lane.getQueuePoints().getLast().getXFloored(), lane.getQueuePoints().getLast().getYFloored());
                    graphics2D.setColor(Color.black);
                }

                graphics2D.setColor(Color.white);
                for (int j = 0; j < entrance.getExitPathsPoints().size(); j++) {
                    graphics2D.drawString(String.valueOf(j), entrance.getExitPathsPoints().get(j).getFirst().getXFloored(), entrance.getExitPathsPoints().get(j).getFirst().getYFloored());
                }
                int middleX = (entrance.getStart().getXFloored() + entrance.getEnd().getXFloored()) / 2;
                int middleY = (entrance.getStart().getYFloored() + entrance.getEnd().getYFloored()) / 2;

                graphics2D.drawString(String.valueOf(entrance.getId()), middleX, middleY);
                graphics2D.setColor(Color.black);
            }

            if (Main.getCarsObjectsList() != null && !Main.getCarsObjectsList().isEmpty())
            {
                for (Car car : Main.getCarsObjectsList()) {
                    graphics2D.setColor(MyColors.getUniqueColor(car.getCarId()));
                    graphics2D.fillRect(car.getPosition().getXFloored() - (carWidth / 2), car.getPosition().getYFloored() - (carHeight / 2), carWidth, carHeight);
                }
            }
        }
    }
}


