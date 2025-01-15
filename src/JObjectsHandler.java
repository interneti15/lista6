import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class JObjectsHandler {
    private MainApplicationWindow ApplicationMainJFrame;

    JObjectsHandler() {
        System.out.println("JObjectsHandler constructor started");
        AtomicBoolean isInvokerRunning = new AtomicBoolean(true);
        SwingUtilities.invokeLater(() -> {

                System.out.println("Creating MainApplicationWindow");
                this.ApplicationMainJFrame = new MainApplicationWindow("Cars Simulation");

                System.out.println("Creating Intersection Object");
                int numberOfEntrances = askUserForNumber("Number of entrances", "Enter the number of entrances!", 2);
                Main.setNumberOfCars(askUserForNumber("Number of cars", "Enter the number of cars!", -1));
                Entrance.setRoadLanes(askUserForNumber("Number of lanes", "Enter the maximum number of lanes going to each side of the intersection!", 0));
                Main.setIntersection(new Intersection(numberOfEntrances, ApplicationMainJFrame));

                isInvokerRunning.set(false);
        });

        while (isInvokerRunning.get()) {}

        if (Main.getIntersection() == null) {
            throw new IllegalStateException("");
        }

        System.out.println("JObjectsHandler constructor has finished");
    }

    public MainApplicationWindow getApplicationMainJFrame() {
        return ApplicationMainJFrame;
    }

    public int askUserForNumber(String name, String question, int minimum) {
        String input = JOptionPane.showInputDialog(ApplicationMainJFrame,
                question,
                name,
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isEmpty()){
            JOptionPane.showMessageDialog(ApplicationMainJFrame,
                    "Invalid input.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return askUserForNumber(name, question, minimum);
        }

        int numberOfEntrances;
        try {
            int userInput = Integer.parseInt(input);
            if (userInput > minimum) {
                numberOfEntrances = userInput;
            } else {
                JOptionPane.showMessageDialog(ApplicationMainJFrame,
                        "Invalid input. Number is too small.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);

                return askUserForNumber(name, question, minimum);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ApplicationMainJFrame,
                    "Invalid input.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);

            return askUserForNumber(name, question, minimum);
        }

        return numberOfEntrances;
    }

}
