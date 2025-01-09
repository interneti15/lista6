import javax.swing.*;

public class MainApplicationWindow extends JFrame {

    private static final int windowSize = 750;

    MainApplicationWindow(String name) {
        super(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(windowSize + 14, windowSize + 37);
        this.setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        this.setContentPane(layeredPane);
        this.setResizable(false);
        this.getContentPane().setBackground(MyColors.MainBackground);
        this.setVisible(true);
    }

    public int getWindowSize() {
        return windowSize;
    }
    public Point getMiddlePoint() {
        return new Point((double) windowSize / 2, (double) windowSize / 2);
    }

    /*@Override
    public int getHeight() {
        throw (new UnsupportedOperationException("Use getWindowSize() instead."));
    }

    @Override
    public int getWidth() {
        throw (new UnsupportedOperationException("Use getWindowSize() instead."));
    }*/
}
