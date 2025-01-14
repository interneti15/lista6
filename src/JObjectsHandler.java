import javax.swing.*;

public class JObjectsHandler {
    private MainApplicationWindow ApplicationMainJFrame;

    JObjectsHandler() {
        System.out.println("JObjectsHandler constructor started");

        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating MainApplicationWindow");
            this.ApplicationMainJFrame = new MainApplicationWindow("Cars Simulation");

            System.out.println("Creating Intersection Object");
            Main.setIntersection(new Intersection(askUserForNumber("Number of entrances", "Enter the number of entrances!"), ApplicationMainJFrame));
        });

        System.out.println("JObjectsHandler constructor has finished");
    }

    public MainApplicationWindow getApplicationMainJFrame() {
        return ApplicationMainJFrame;
    }

    private int askUserForNumber(String name, String question) {
        String input = JOptionPane.showInputDialog(ApplicationMainJFrame,
                question,
                name,
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isEmpty()){
            JOptionPane.showMessageDialog(ApplicationMainJFrame,
                    "Invalid input.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return askUserForNumber(name, question);
        }

        int numberOfEntrances;
        try {
            int userInput = Integer.parseInt(input);
            if (userInput > 2) {
                numberOfEntrances = userInput;
            } else {
                JOptionPane.showMessageDialog(ApplicationMainJFrame,
                        "Invalid input. Number is too small.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);

                return askUserForNumber(name, question);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ApplicationMainJFrame,
                    "Invalid input.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);

            return askUserForNumber(name, question);
        }

        return numberOfEntrances;
    }

}
