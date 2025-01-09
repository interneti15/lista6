import javax.swing.*;

public class JObjectsHandler {
    private MainApplicationWindow ApplicationMainJFrame;

    JObjectsHandler() {
        System.out.println("JObjectsHandler constructor started");

        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating MainApplicationWindow");
            this.ApplicationMainJFrame = new MainApplicationWindow("Zadanie 1");
        });
    }

    public MainApplicationWindow getApplicationMainJFrame() {
        return ApplicationMainJFrame;
    }


}
