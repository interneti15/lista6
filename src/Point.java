public class Point {
    private double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Point(Point point){
        this.x = point.x;
        this.y = point.y;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getXFloored() {
        return (int) x;
    }
    public int getYFloored() {
        return (int) y;
    }

    @Override
    public String toString() {
        return "Point{x=" + x + ", y=" + y + "}";
    }

    public static class Line{
        Point start;
        Point end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }
}
