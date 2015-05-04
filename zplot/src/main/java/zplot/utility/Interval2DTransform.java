package zplot.utility;

public class Interval2DTransform {

    public Interval2DTransform(IntervalTransform x, IntervalTransform y) {
	this.x = x;
	this.y = y;
    }

    public Interval2D transform(Interval2D d) {
	return new Interval2D(x.transform(d.x()), y.transform(d.y()));
    }

    public Interval2D transformFlipY(Interval2D d) {
	return new Interval2D(x.transform(d.x()), y.transformFlip(d.y()));
    }

    public IntervalTransform x() {
	return x;
    }

    public IntervalTransform y() {
	return y;
    }

    public Interval2DTransform inverseTransform() {
	return new Interval2DTransform(x.inverseTransform(), y.inverseTransform());
    }

    @Override
    public String toString() {
	return "Interval2DTransform{" + "x=" + x + ", y=" + y + '}';
    }

    private final IntervalTransform x;
    private final IntervalTransform y;
}
