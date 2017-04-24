package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.ZMath;

/**
 * A class to represent an irregularly sampled time-series or x-y scatter data.
 * The x and y coordinate values of points are stored interleaved in an array.
 *
 * The class is abstract but contains two static inner classes
 * InterleavedSeries.Double and InterleavedSeries.Float which store values in
 * either double, or float precision, respectively. All constructors are
 * private, instances are created using one of the static factory methods.
 *
 * We allow access to the underlying data array, which presents opportunity for
 * optimisation in the plotting code. Any code accessing this array must promise
 * not to modify it.
 */
public abstract class InterleavedSeries implements Series {

	/**
	 * Create an instance from interleaved (x,y)-values.
	 *
	 * @param interleaved The interleaved x and y values.
	 * @return An InterleavedSeries instance.
	 */
	public static InterleavedSeries.Double makeScatterSeries(double[] interleaved) {
		checkInterleavedValuesArgument(interleaved);
		return new InterleavedSeries.Double(interleaved);
	}

	/**
	 * Create an instance from interleaved (x,y)-values where the x values are
	 * increasing.
	 *
	 * @param interleaved The interleaved (x,y)-values. The caller must ensure
	 * that the x values are increasing.
	 * @return An InterleavedSeries instance.
	 */
	public static InterleavedSeries.Double makeOrderedSeries(double[] interleaved) {
		checkInterleavedValuesArgument(interleaved);
		return new InterleavedSeries.OrderedDouble(interleaved);
	}

	/**
	 * Overload for float values.
	 *
	 * @see InterleavedSeries#makeScatterSeries(double[])
	 */
	public static InterleavedSeries.Float makeScatterSeries(float[] interleaved) {
		checkInterleavedValuesArgument(interleaved);
		return new InterleavedSeries.Float(interleaved);
	}

	/**
	 * Overload for float values.
	 *
	 * @see InterleavedSeries#makeOrderedSeries(double[])
	 */
	public static InterleavedSeries.Float makeOrderedSeries(float[] interleaved) {
		checkInterleavedValuesArgument(interleaved);
		return new InterleavedSeries.OrderedFloat(interleaved);
	}

	private static void checkInterleavedValuesArgument(double[] interleaved) {
		if (interleaved == null) {
			throw new NullPointerException("interleaved cannot be null");
		} else if (interleaved.length == 0) {
			throw new IllegalArgumentException("interleaved cannot be empty");
		} else if ((interleaved.length & 1) == 1) {
			throw new IllegalArgumentException("interleaved must have even length");
		}
	}

	private static void checkInterleavedValuesArgument(float[] interleaved) {
		if (interleaved == null) {
			throw new NullPointerException("interleaved cannot be null");
		} else if (interleaved.length == 0) {
			throw new IllegalArgumentException("interleaved cannot be empty");
		} else if ((interleaved.length & 1) == 1) {
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

	public static class OrderedDouble extends InterleavedSeries.Double implements OrderedSeries {

		private OrderedDouble(double[] interleaved) {
			super(interleaved);
		}

		@Override
		public int pullBack(double x) {
			return pullBackImpl(this, x);
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

	public static class OrderedFloat extends InterleavedSeries.Float implements OrderedSeries {

		private OrderedFloat(float[] interleaved) {
			super(interleaved);
		}

		@Override
		public int pullBack(double x) {
			return pullBackImpl(this, x);
		}
	}

	private static int pullBackImpl(Series series, double x) {
		for (int i = 0; i != series.size(); ++i) {
			double xx = series.x(i);
			if (xx == x) {
				return i;
			} else if (xx > x) {
				return Math.max(0, i - 1);
			}
		}
		return series.size() - 1;
	}
}
