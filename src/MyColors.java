import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyColors {
    public static final Color MainBackground = new Color(230, 230, 230);
    public static final Color MainForeground = new Color(70, 70, 70);
    private static final double MINIMAL_DISTANCE = 40;
    private static final int MAX_RETRIES = 100;
    private static final int MAX_COLOR_VALUE = 205;
    private static final ArrayList<Color> uniqueColors = new ArrayList<>();

    public static ArrayList<Color> getUniqueColorsList() {
        return uniqueColors;
    }

    private static Double colorDistance(Color A, Color B) {
        return Math.sqrt(Math.pow((A.getRed() - B.getRed()), 2) + Math.pow((A.getBlue() - B.getBlue()), 2) + Math.pow((A.getGreen() - B.getGreen()), 2));
    }

    private static Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(MAX_COLOR_VALUE), random.nextInt(MAX_COLOR_VALUE), random.nextInt(MAX_COLOR_VALUE));
    }

    public static Color getUniqueColor(int i) {
        if (i >= uniqueColors.size()) {
            generateUniqueColor();
            return getUniqueColor(i);
        }
        return uniqueColors.get(i);
    }

    private static Color generateUniqueColor() {

        if (uniqueColors.isEmpty()) {
            Color initialColor = getRandomColor();
            uniqueColors.add(initialColor);
            return initialColor;
        }
        double maximalMinimalDistance = Double.MIN_VALUE;
        Color minimalColor = null;
        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            Color tempColor = getRandomColor();

            boolean isUnique = true;
            double minimalDistanceNow = Double.MAX_VALUE;
            for (Color uniqueColor : uniqueColors) {
                double colorDistanceNow = colorDistance(tempColor, uniqueColor);
                if (colorDistanceNow < minimalDistanceNow) {
                    minimalDistanceNow = colorDistanceNow;
                }
                if (colorDistanceNow < MINIMAL_DISTANCE) {
                    isUnique = false;
                }
            }
            if (minimalDistanceNow > maximalMinimalDistance) {
                maximalMinimalDistance = minimalDistanceNow;
                minimalColor = tempColor;
            }

            if (isUnique) {
                uniqueColors.add(tempColor);
                return tempColor;
            }
        }

        if (minimalColor == null) {
            minimalColor = getRandomColor();
        }
        uniqueColors.add(minimalColor);
        return minimalColor;
    }

    public static String nameColor(Color color) {
        Color[] standardColors = {
                new Color(255, 0, 0),     // Red
                new Color(139, 0, 0),     // Dark Red
                new Color(255, 99, 71),   // Tomato
                new Color(220, 20, 60),   // Crimson
                new Color(240, 128, 128), // Light Coral
                new Color(0, 255, 0),     // Green
                new Color(0, 128, 0),     // Dark Green
                new Color(34, 139, 34),   // Forest Green
                new Color(144, 238, 144), // Light Green
                new Color(50, 205, 50),   // Lime Green
                new Color(0, 0, 255),     // Blue
                new Color(0, 0, 139),     // Dark Blue
                new Color(70, 130, 180),  // Steel Blue
                new Color(100, 149, 237), // Cornflower Blue
                new Color(135, 206, 250), // Light Sky Blue
                new Color(255, 255, 0),   // Yellow
                new Color(255, 165, 0),   // Orange
                new Color(255, 140, 0),   // Dark Orange
                new Color(255, 215, 0),   // Gold
                new Color(255, 250, 205), // Lemon Chiffon
                new Color(128, 0, 128),   // Purple
                new Color(147, 112, 219), // Medium Purple
                new Color(75, 0, 130),    // Indigo
                new Color(138, 43, 226),  // Blue Violet
                new Color(186, 85, 211),  // Medium Orchid
                new Color(255, 192, 203), // Pink
                new Color(255, 105, 180), // Hot Pink
                new Color(255, 20, 147),  // Deep Pink
                new Color(219, 112, 147), // Pale Violet Red
                new Color(255, 182, 193), // Light Pink
                new Color(0, 0, 0),       // Black
                new Color(255, 255, 255), // White
                new Color(128, 128, 128), // Gray
                new Color(169, 169, 169), // Dark Gray
                new Color(105, 105, 105), // Dim Gray
                new Color(220, 220, 220), // Light Gray
                new Color(211, 211, 211), // Gainsboro
                new Color(47, 79, 79),    // Dark Slate Gray
                new Color(112, 128, 144), // Slate Gray
                new Color(119, 136, 153), // Light Slate Gray
                new Color(128, 0, 0),     // Maroon
                new Color(0, 128, 128),   // Teal
                new Color(128, 128, 0),   // Olive
                new Color(0, 128, 255),   // Azure Blue
                new Color(255, 0, 128),   // Rose Red
                new Color(128, 0, 255),   // Violet Blue
                new Color(255, 128, 0),   // Amber
                new Color(128, 255, 0),   // Chartreuse
                new Color(0, 255, 128),   // Spring Bud
                new Color(128, 255, 255), // Cyan Light
                new Color(192, 192, 192), // Silver
                new Color(255, 223, 186), // Peach
                new Color(123, 104, 238), // Medium Slate Blue
                new Color(0, 255, 255),   // Aqua
                new Color(255, 240, 245), // Lavender Blush
                new Color(250, 235, 215), // Antique White
                new Color(233, 150, 122), // Dark Salmon
                new Color(165, 42, 42),   // Brown
                new Color(255, 99, 71),   // Tomato
                new Color(152, 251, 152), // Pale Green
                new Color(0, 206, 209),   // Dark Turquoise
                new Color(244, 164, 96),  // Sandy Brown
                new Color(46, 139, 87),   // Sea Green
                new Color(32, 178, 170),  // Light Sea Green
                new Color(135, 206, 235), // Sky Blue
                new Color(245, 245, 220), // Beige
                new Color(222, 184, 135), // Burlywood
                new Color(255, 228, 225), // Misty Rose
                new Color(255, 228, 196), // Bisque
                new Color(210, 180, 140), // Tan
                new Color(128, 0, 0),     // Brick Red
                new Color(0, 255, 255),   // Electric Blue
                new Color(72, 61, 139),   // Dark Slate Blue
                new Color(123, 104, 238), // Orchid
                new Color(199, 21, 133)   // Raspberry
        };

        String[] colorNames = {
                "Red", "Dark Red", "Tomato", "Crimson", "Light Coral",
                "Green", "Dark Green", "Forest Green", "Light Green", "Lime Green",
                "Blue", "Dark Blue", "Steel Blue", "Cornflower Blue", "Light Sky Blue",
                "Yellow", "Orange", "Dark Orange", "Gold", "Lemon Chiffon",
                "Purple", "Medium Purple", "Indigo", "Blue Violet", "Medium Orchid",
                "Pink", "Hot Pink", "Deep Pink", "Pale Violet Red", "Light Pink",
                "Black", "White", "Gray", "Dark Gray", "Dim Gray",
                "Light Gray", "Gainsboro", "Dark Slate Gray", "Slate Gray", "Light Slate Gray",
                "Maroon", "Teal", "Olive", "Azure Blue", "Rose Red",
                "Violet Blue", "Amber", "Chartreuse", "Spring Bud", "Cyan Light",
                "Silver", "Peach", "Medium Slate Blue", "Aqua", "Lavender Blush",
                "Antique White", "Dark Salmon", "Brown", "Tomato", "Pale Green",
                "Dark Turquoise", "Sandy Brown", "Sea Green", "Light Sea Green", "Sky Blue",
                "Beige", "Burlywood", "Misty Rose", "Bisque", "Tan",
                "Brick Red", "Electric Blue", "Dark Slate Blue", "Orchid", "Raspberry"
        };

        double minDistance = Double.MAX_VALUE;
        String closestColorName = "Unknown";

        for (int i = 0; i < standardColors.length; i++) {
            double distance = colorDistance(color, standardColors[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestColorName = colorNames[i];
            }
        }

        return closestColorName;
    }
}

