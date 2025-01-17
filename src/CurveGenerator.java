import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
public class CurveGenerator {

    /**
     * Generates a cubic Bézier curve connecting two points with specified angles.
     *
     * @param x1 Start point x-coordinate.
     * @param y1 Start point y-coordinate.
     * @param angle1 Angle in degrees from the start point.
     * @param x2 End point x-coordinate.
     * @param y2 End point y-coordinate.
     * @param angle2 Angle in degrees toward the end point.
     * @param offset Increased offset for more pronounced curvature
     * @return A CubicCurve2D.Float object representing the curve.
     */
    private static CubicCurve2D.Double generateCurve(double x1, double y1, double angle1,
                                                    double x2, double y2, double angle2, double offset) {
        // Define control point offsets based on angle
        // Increased offset for more pronounced curvature

        // Calculate control points for the start point
        double ctrl1X = x1 + (offset * Math.cos((angle1)));
        double ctrl1Y = y1 - (offset * Math.sin((angle1)));

        // Calculate control points for the end point
        double ctrl2X = x2 + (offset * Math.cos((angle2)));
        double ctrl2Y = y2 - (offset * Math.sin((angle2)));

        // Create and return the curve
        return new CubicCurve2D.Double(x1, y1, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2);
    }

    /**
     * Samples points along a cubic Bezier curve.
     *
     * @param curve The cubic Bezier curve to sample.
     * @param samplingRate Number of points to sample along the curve.
     * @return A list of Point objects representing sampled points.
     */
    private static ArrayList<Point> sampleCurve(CubicCurve2D.Double curve, int samplingRate) {
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 0; i <= samplingRate; i++) {
            double t = i / (double) samplingRate;
            double x = Math.pow(1 - t, 3) * curve.getX1()
                    + 3 * Math.pow(1 - t, 2) * t * curve.getCtrlX1()
                    + 3 * (1 - t) * Math.pow(t, 2) * curve.getCtrlX2()
                    + Math.pow(t, 3) * curve.getX2();

            double y = Math.pow(1 - t, 3) * curve.getY1()
                    + 3 * Math.pow(1 - t, 2) * t * curve.getCtrlY1()
                    + 3 * (1 - t) * Math.pow(t, 2) * curve.getCtrlY2()
                    + Math.pow(t, 3) * curve.getY2();

            points.add(new Point(x, y));
        }

        return points;
    }

    public static ArrayList<Point> generateCurveAndPoints(double x1, double y1, double angle1,
                                                          double x2, double y2, double angle2,
                                                          int samplingRate, double offset){
        return sampleCurve(generateCurve(x1, y1, angle1, x2, y2, angle2, offset), samplingRate);
    }
}