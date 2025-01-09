public class Entrance {
    private final Point start;
    private final Point end;
    private final int id;
    private final int number;

    Entrance(Point start, Point end, int id, int number) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.number = number;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
