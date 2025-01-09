import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class Intersection extends Thread {
    private ArrayList<Entrance> entrances = new ArrayList<>();
    private IntersectionHandler intersectionJPanelHandler;


    Intersection(int entrances, MainApplicationWindow mainApplicationWindow) {

        this.intersectionJPanelHandler = new IntersectionHandler(entrances, mainApplicationWindow, this);


    }

    public IntersectionHandler getIntersectionJPanelHandler() {
        return intersectionJPanelHandler;
    }

    public ArrayList<Entrance> getEntrances() {
        return entrances;
    }
}

class IntersectionHandler extends JPanel {
    private static final double intersectionSizeRatio = 0.65;
    private double intersectionSize;
    private Point middlePoint;
    private int entrancesNumber;
    private final ArrayList<Point> vertices = new ArrayList<>();
    private Intersection intersection;

    IntersectionHandler(int entrancesNumber, MainApplicationWindow mainApplicationWindow, Intersection intersection) {
        this.intersection = intersection;
        this.setBackground(MyColors.MainForeground);
        this.setBounds(0, 0, mainApplicationWindow.getWindowSize(), mainApplicationWindow.getWindowSize());
        this.setLayout(null);
        mainApplicationWindow.add(this);

        this.middlePoint = mainApplicationWindow.getMiddlePoint();
        this.intersectionSize = mainApplicationWindow.getWindowSize() * intersectionSizeRatio / 2;
        this.entrancesNumber = entrancesNumber;

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (true) {
                    return;
                }
                int x = e.getX();
                int y = e.getY();
                System.out.println("Mouse moved: (" + x + ", " + y + ")");
            }
        });

        calculateVertices();
    }

    private void calculateVertices() {
        double angleStep = 2 * Math.PI / entrancesNumber;
        double angle = Math.PI / entrancesNumber;
        //angle = 0;

        for (int i = 0; i < entrancesNumber; i++) {
            double x = middlePoint.getX() + intersectionSize * Math.cos(angle);
            double y = middlePoint.getY() + intersectionSize * Math.sin(angle);
            vertices.add(new Point(x, y));
            angle += angleStep;
        }

        calculateEntrances(this.vertices);
    }

    private void calculateEntrances(ArrayList<Point> vertices) {
        intersection.getEntrances().clear();
        for (int i = 1; i < this.vertices.size(); i++) {
            intersection.getEntrances().add(new Entrance(vertices.get(i - 1), vertices.get(i), i - 1, this.vertices.size()));
        }
        intersection.getEntrances().add(new Entrance(vertices.getLast(), vertices.getFirst(), this.vertices.size() - 1, this.vertices.size()));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        //this.setBounds(0,0, Main.getjObjectsHandler().getApplicationMainJFrame().getWidth(), Main.getjObjectsHandler().getApplicationMainJFrame().getHeight());
        Graphics2D graphics2D = (Graphics2D) graphics;
        if (vertices.size() < 2) {
            return;
        }
        graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(4));
        /*for (int i = 1; i < vertices.size(); i++) {

            graphics2D.drawLine((int) vertices.get(i - 1).getX(), (int) vertices.get(i - 1).getY(), (int) vertices.get(i).getX(), (int) vertices.get(i).getY());
        }
        graphics2D.drawLine((int) vertices.getFirst().getX(), (int) vertices.getFirst().getY(), (int) vertices.getLast().getX(), (int) vertices.getLast().getY());*/
        graphics2D.drawOval((int) middlePoint.getX(), (int) middlePoint.getY(), 1, 1);
        for (Entrance entrance : intersection.getEntrances()){
            graphics2D.drawLine((int) entrance.getStart().getX(), (int) entrance.getStart().getY(), (int) entrance.getEnd().getX(), (int) entrance.getEnd().getY());
        }
    }
}
