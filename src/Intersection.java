import javax.swing.*;
import java.util.ArrayList;

public class Intersection extends Thread {
    private ArrayList<Entrance> entrances;
    private IntersectionHandler intersectionHandler;


    Intersection(int entrances, MainApplicationWindow mainApplicationWindow){
        SwingUtilities.invokeLater(() -> {
            this.intersectionHandler = new IntersectionHandler(entrances, mainApplicationWindow);
        });

    }
}

class IntersectionHandler extends JPanel{
    private static final double intersectionSizeRatio = 0.5;
    private double intersectionSize;

    IntersectionHandler(int entrances, MainApplicationWindow mainApplicationWindow){
        this.setBackground(MyColors.MainForeground);
        this.setBounds(0,0, mainApplicationWindow.getWidth(), mainApplicationWindow.getHeight());
        this.setLayout(null);
        mainApplicationWindow.add(this);

        Point middlePoint = mainApplicationWindow.getMiddlePoint();
        this.intersectionSize = mainApplicationWindow.getWindowSize() * intersectionSizeRatio;
    }
}
