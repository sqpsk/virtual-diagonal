package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.ZMath;

/**
 * An irregularly sampled time-series, or x-y scatter data. The x and y
 * coordinate values of points are stored interleaved in an array.
 *
 * The class is abstract but contains two static inner classes
 * InterleavedSeries.Double and InterleavedSeries.Float which store values in
 * either double, or float precision, respectively. All constructors are
 * private, instances are created using one of the static factory methods.
 *
 * We allow access to the underlying data array, which presents opportunity for
 * optimization in the plotting code. Any code accessing this array must promise
 * not to modify it.
 */
public abstract class InterleavedSeries implements ISeries {

	public static InterleavedSeries.Double makeScatterSeries(double[] interleaved) {
		checkArguments(interleaved);
		return new InterleavedSeries.Double(interleaved);
	}

	public static InterleavedSeries.Float makeScatterSeries(float[] interleaved) {
		checkArguments(interleaved);
		return new InterleavedSeries.Float(interleaved);
	}

	public static InterleavedSeries.Double makeOrderedSeries(double[] interleaved) {
		checkArguments(interleaved);
		return new InterleavedSeries.OrderedDouble(interleaved);
	}

	public static InterleavedSeries.Float makeOrderedSeries(float[] interleaved) {
		checkArguments(interleaved);
		return new InterleavedSeries.OrderedFloat(interleaved);
	}

	private static void checkArguments(double[] interleaved) {
		if ((interleaved.length & 1) == 1) {
			throw new IllegalArgumentException("interleaved must have even length");
		}
	}

	private static void checkArguments(float[] interleaved) {
		if ((interleaved.length & 1) == 1) {
			throw new IllegalArgumentException("interleaved must have even length");
		}
	}

	private InterleavedSeries(Interval2D range) {
		this.range = range;
	}

	@Override
	public Interval2D range() {
		return range;
	}

	@Override
	public Interval xRange() {
		return range.x();
	}

	@Override
	public Interval yRange() {
		return range.y();
	}

	public int pullBack(double x) {
		for (int i = 0; i != size(); ++i) {
			double xx = x(i);
			if (xx == x) {
				return i;
			} else if (xx > x) {
				return i - 1;
			}
		}
		return 0;
	}

	private final Interval2D range;

	public static class Double extends InterleavedSeries {

		private Double(double[] interleaved) {
			super(ZMath.calculateInterleavedRange(interleaved, 0, interleaved.length));
			this.interleaved = interleaved;
		}

		@Override
		public int size() {
			return interleaved.length / 2;
		}

		@Override
		public boolean isEmpty() {
			return interleaved.length == 0;
		}

		@Override
		public double x(int i) {
			return interleaved[2 * i];
		}

		@Override
		public double y(int i) {
			return interleaved[2 * i + 1];
		}
		// Data stored in (x, y) interleaved format.
		private final double[] interleaved;
	}

	public static class OrderedDouble extends InterleavedSeries.Double implements IOrderedSeries {

		private OrderedDouble(double[] interleaved) {
			super(interleaved);
		}
	}

	public static class Float extends InterleavedSeries {

		private Float(float[] interleaved) {
			super(ZMath.calculateInterleavedRange(interleaved, 0, interleaved.length));
			this.interleaved = interleaved;
		}

		@Override
		public int size() {
			return interleaved.length / 2;
		}

		@Override
		public boolean isEmpty() {
			return interleaved.length == 0;
		}

		@Override
		public double x(int i) {
			return interleaved[2 * i];
		}

		@Override
		public double y(int i) {
			return interleaved[2 * i + 1];
		}
		// Data stored in (x, y) interleaved format.
		private final float[] interleaved;
	}

	public static class OrderedFloat extends InterleavedSeries.Float implements IOrderedSeries {

		private OrderedFloat(float[] interleaved) {
			super(interleaved);
		}
	}
}
