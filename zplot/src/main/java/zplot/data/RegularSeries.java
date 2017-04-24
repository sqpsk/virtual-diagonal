package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZMath;

/**
 * A class representing a regularly sampled time-series. Stores the y-values and
 * the minimum and maximum x-values. Since the data is assumed to be regularly
 * sampled, the x-value of each point is calculated from the x-range when
 * required.
 *
 * The class is abstract but contains two static inner classes
 * RegularSeries.Double and RegularSeries.Float which hold the y-values in
 * either double, or float precision, respectively. All constructors are
 * private, instances are created using one of the static factory methods.
 *
 * We allow access to the underlying data array, which presents opportunity for
 * optimization in the plotting code. Any code accessing this array must promise
 * not to modify it.
 */
public abstract class RegularSeries implements OrderedSeries {

	/**
	 * Create an instance from y-values, the first x-value and the x-step.
	 *
	 * @param yValues The y-values of the points.
	 * @param xBegin The x-value of the first point.
	 * @param xStep The difference between adjacent x-values.
	 * @return A RegularSeries instance.
	 */
	public static Double makeFromBeginAndStep(double[] yValues, double xBegin, double xStep) {
		checkYvaluesArgument(yValues);
		if (xStep < 0.0) {
			throw new IllegalArgumentException("xStep=" + xStep + ", must be >= 0");
		}
		return new Double(yValues, xBegin, xBegin + (yValues.length - 1) * xStep);
	}

	/**
	 * Create an instance from y-values, the first x-value and last x-value.
	 *
	 * @param yValues The y-values of the points.
	 * @param xBegin The x-value of the first point.
	 * @param xBack The x-value of the last point.
	 * @return A RegularSeries instance.
	 */
	public static Double makeFromBeginAndBack(double[] yValues, double xBegin, double xBack) {
		checkYvaluesArgument(yValues);
		if (xBack < xBegin) {
			throw new IllegalArgumentException("xBegin=" + xBegin + ", xBack=" + xBack + ", must have xBegin <= xBack");
		}
		return new Double(yValues, xBegin, xBack);
	}

	/**
	 * Create an instance from y-values, the first x-value and one step past the
	 * last x-value.
	 *
	 * @param yValues The y-values of the points.
	 * @param xBegin The x-value of the first point.
	 * @param xEnd The x-value of a point one step after the last point in the
	 * series.
	 * @return A RegularSeries instance.
	 */
	public static Double makeFromBeginAndEnd(double[] yValues, double xBegin, double xEnd) {
		checkYvaluesArgument(yValues);
		if (xEnd <= xBegin) {
			throw new IllegalArgumentException("xBegin=" + xBegin + ", xEnd=" + xEnd + ", must have xBegin < xEnd");
		}
		return makeFromBeginAndStep(yValues, xBegin, (xEnd - xBegin) / yValues.length);
	}

	/**
	 * Overload for float y-values.
	 *
	 * @see RegularSeries#makeFromBeginAndStep(double[], double, double)
	 */
	public static Float makeFromBeginAndStep(float[] yValues, double xBegin, double xStep) {
		checkYvaluesArgument(yValues);
		if (xStep < 0.0) {
			throw new IllegalArgumentException("xStep=" + xStep + ", must be >= 0");
		}
		return new Float(yValues, xBegin, xBegin + (yValues.length - 1) * xStep);
	}

	/**
	 * Overload for float y-values.
	 *
	 * @see RegularSeries#makeFromBeginAndBack(double[], double, double)
	 */
	public static Float makeFromBeginAndBack(float[] yValues, double xBegin, double xBack) {
		checkYvaluesArgument(yValues);
		if (xBack < xBegin) {
			throw new IllegalArgumentException("xBegin=" + xBegin + ", xBack=" + xBack + ", must have xBegin <= xBack");
		}
		return new Float(yValues, xBegin, xBack);
	}

	/**
	 * Overload for float y-values.
	 *
	 * @see RegularSeries#makeFromBeginAndEnd(double[], double, double)
	 */
	public static Float makeFromBeginAndEnd(float[] yValues, double xBegin, double xEnd) {
		checkYvaluesArgument(yValues);
		if (xEnd <= xBegin) {
			throw new IllegalArgumentException("xBegin=" + xBegin + ", xEnd=" + xEnd + ", must have xBegin < xEnd");
		}
		return makeFromBeginAndStep(yValues, xBegin, (xEnd - xBegin) / yValues.length);
	}

	private static void checkYvaluesArgument(double[] yValues) {
		if (yValues == null) {
			throw new NullPointerException("yValues cannot be null");
		} else if (yValues.length == 0) {
			throw new IllegalArgumentException("yValues cannnot be empty");
		}
	}

	private static void checkYvaluesArgument(float[] yValues) {
		if (yValues == null) {
			throw new NullPointerException("yValues cannot be null");
		} else if (yValues.length == 0) {
			throw new IllegalArgumentException("yValues cannnot be empty");
		}
	}

	private RegularSeries(Interval2D range, int size) {
		this.range = range;
		this.scale = size > 1 ? size - 1 : 1;
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

	@Override
	public double x(int i) {
		return range.x().begin() + i * range.x().size() / scale;
	}

	@Override
	public int pullBack(double x) {
		IntervalTransform it = new IntervalTransform(xRange(), 0, size() - 1);
		return ZMath.bound(0, (int) Math.floor(it.transform(x)), size() - 1);
	}

	private final Interval2D range;
	private final int scale;

	public static class Double extends RegularSeries {

		private Double(double[] data, Interval xRange) {
			super(new Interval2D(xRange, ZMath.calculateRange(data, 0, data.length)), data.length);
			this.yValues = data;
		}

		private Double(double[] data, double xBegin, double xBack) {
			this(data, new Interval(xBegin, xBack));
		}

		@Override
		public int size() {
			return yValues.length;
		}

		@Override
		public double y(int i) {
			return yValues[i];
		}

		public double[] yValues() {
			return yValues;
		}
		private final double[] yValues;
	}

	public static class Float extends RegularSeries {

		private Float(float[] data, Interval xRange) {
			super(new Interval2D(xRange, ZMath.calculateRange(data, 0, data.length)), data.length);
			this.yValues = data;
		}

		private Float(float[] data, double xBegin, double xBack) {
			this(data, new Interval(xBegin, xBack));
		}

		@Override
		public int size() {
			return yValues.length;
		}

		@Override
		public double y(int i) {
			return yValues[i];
		}

		public float[] yValues() {
			return yValues;
		}
		private final float[] yValues;
	}
}
