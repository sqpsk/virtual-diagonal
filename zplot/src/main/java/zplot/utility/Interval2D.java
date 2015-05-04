package zplot.utility;

public class Interval2D {

    public static Interval2D invalid() {
	return new Interval2D(Interval.invalid(), Interval.invalid());
    }

    public static Interval2D envelope(Interval2D a, Interval2D b) {
	return new Interval2D(Interval.envelope(a.x(), b.x()),
		Interval.envelope(a.y(), b.y()));
    }

    public static Interval2D intersection(Interval2D a, Interval2D b) {
	return new Interval2D(Interval.intersection(a.x(), b.x()),
		Interval.intersection(a.y(), b.y()));
    }

    public static Interval2D fromCornerAndSize(double x, double y, double width, double height) {
	return new Interval2D(x, x + width, y, y + height);
    }

    public Interval2D(double xBegin, double xEnd, double yBegin, double yEnd) {
	this.x = new Interval(xBegin, xEnd);
	this.y = new Interval(yBegin, yEnd);
    }

    public Interval2D(Interval x, Interval y) {
	this.x = x;
	this.y = y;
    }

    public Interval2D(double xBegin, double xEnd, Interval y) {
	this.x = new Interval(xBegin, xEnd);
	this.y = y;
    }

    public Interval2D(Interval x, double yBegin, double yEnd) {
	this.x = x;
	this.y = new Interval(yBegin, yEnd);
    }

    public Interval x() {
	return x;
    }

    public Interval y() {
	return y;
    }

    public double width() {
	return x.size();
    }

    public double height() {
	return y.size();
    }

    @Override
    public String toString() {
	return x.toString() + " x " + y.toString();
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 19 * hash + x.hashCode();
	hash = 19 * hash + y.hashCode();
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	return obj != null
		&& getClass() == obj.getClass()
		&& equals((Interval2D) obj);
    }

    public boolean equals(Interval2D other) {
	return x.equals(other.x()) && y.equals(other.y());
    }

    private final Interval x;
    private final Interval y;
}
