import java.util.ArrayList;

public class MathUtils {

    public static boolean doLinesIntersect(Point.Line line1, Point.Line line2){
        return doLinesIntersect(line1.start, line1.end, line2.start, line2.end);
    }

    public static boolean doLinesIntersect(Point p1, Point p2, Point p3, Point p4) {
        // Calculate the direction of the lines
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        // General case: lines intersect if the directions differ
        if (d1 * d2 < 0 && d3 * d4 < 0) {
            return true;
        }

        // Special cases: check if points are collinear and overlap
        if (d1 == 0 && onSegment(p3, p4, p1)) return true;
        if (d2 == 0 && onSegment(p3, p4, p2)) return true;
        if (d3 == 0 && onSegment(p1, p2, p3)) return true;
        return d4 == 0 && onSegment(p1, p2, p4);
    }

    private static double direction(Point p1, Point p2, Point p3) {
        // Cross product to determine relative orientation
        return (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()) -
                (p3.getY() - p1.getY()) * (p2.getX() - p1.getX());
    }

    private static boolean onSegment(Point p1, Point p2, Point p) {
        // Check if point p is on segment p1-p2
        return Math.min(p1.getX(), p2.getX()) <= p.getX() && p.getX() <= Math.max(p1.getX(), p2.getX()) &&
                Math.min(p1.getY(), p2.getY()) <= p.getY() && p.getY() <= Math.max(p1.getY(), p2.getY());
    }

    public static boolean doesLinesIntersectFromTwoArrays(ArrayList<Point.Line> first, ArrayList<Point.Line> second){
        if (first == null || second == null || first.isEmpty() || second.isEmpty()){
            return false;
        }

        for(Point.Line line1 : first){
            for(Point.Line line2 : second){
                if(doLinesIntersect(line1, line2)){
                    return true;
                }
            }
        }
        return false;
    }
}
