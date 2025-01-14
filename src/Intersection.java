import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;

public class Intersection extends Thread {
    private ArrayList<Entrance> entrances = new ArrayList<>();
    private IntersectionHandler intersectionJPanelHandler;
    private int entrancesNumber;
    private MainApplicationWindow mainApplicationWindow;
    private int totalCarsOnIntersectionThisTurn = 0;
    private final int intersectionTime = 5000;

    Intersection(int entrances, MainApplicationWindow mainApplicationWindow) {
        this.entrancesNumber = entrances;
        this.mainApplicationWindow = mainApplicationWindow;
        //calculateVertices();
        refreshEntrances();
        this.intersectionJPanelHandler = new IntersectionHandler(entrances, mainApplicationWindow, this);
    }

    @Override
    public void run() {
        int maxIterations = 2;
        int iteration = 0;
        while (maxIterations != iteration) {

            manageGreenLights();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            iteration++;
        }
    }

    private void manageGreenLights() {
        ArrayList<Entrance.Lane> laneEntries = new ArrayList<>();
        for (Entrance entrance : entrances) {
            for (Entrance.Lane lane : entrance.getLanes()) {
                lane.calculateLaneHotnesIndex();
                laneEntries.add(lane);
            }
        }

        int sum = 0;
        for (Entrance.Lane lane : laneEntries) {
            sum += lane.getHottness();
        }
        if (sum == 0){
            return;
        }

        laneEntries.sort(Comparator.comparingDouble(Entrance.Lane::getHottness).reversed());
        ArrayList<Point.Line> lines = new ArrayList<>();
        for (Entrance.Lane lane : laneEntries) {
            System.out.println("Lane at entrance #" + lane.getEntrance().getId() + " lane #" + lane.getId() + " hottness " + lane.getHottness());
            if (!MathUtils.doesLinesIntersectFromTwoArrays(lines, lane.pathsToLines())){
                lines.addAll(lane.pathsToLines());
                lane.setGreenLight(true);
                continue;
            }
            lane.setGreenLight(false);
        }

        intersectionJPanelHandler.repaint();
    }
    public void increaseCarsOnIntersection(){
        totalCarsOnIntersectionThisTurn++;
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

            for (Entrance entrance : intersection.getEntrances()) {
                Point start = entrance.getStart();
                Point end = entrance.getEnd();
                if (start != null && end != null) {
                    graphics2D.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
                }
            }
            debugPaths(graphics2D);
        }

        private void debugPaths(Graphics2D graphics2D) {
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

                    if (lane.isGreenLight()){
                        graphics2D.setColor(Color.GREEN);
                    }
                    else {
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

            for (Car car : Main.getCarsObjectsList()) {
                graphics2D.setColor(MyColors.getUniqueColor(car.getCarId()));
                graphics2D.fillRect(car.getPosition().getXFloored(), car.getPosition().getYFloored(), 14, 14);
            }
        }
    }
}


