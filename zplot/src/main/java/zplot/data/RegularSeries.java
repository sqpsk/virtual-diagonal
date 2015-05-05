package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZMath;

/**
 * Represents regularly sampled time-series data.
 *
 */
public abstract class RegularSeries implements IOrderedSeries {

	public static Double makeFromBeginAndStep(double[] data, double xBegin, double xStep) {
		assert data != null && data.length != 0;
		return new Double(data, xBegin, xBegin + (data.length - 1) * xStep);
	}

	public static Double makeFromBeginAndBack(double[] data, double xBegin, double xBack) {
		assert data != null && data.length != 0;
		return new Double(data, xBegin, xBack);
	}

	public static Double makeFromBeginAndEnd(double[] data, double xBegin, double xEnd) {
		assert data != null && data.length != 0;
		return makeFromBeginAndStep(data, xBegin, (xEnd - xBegin) / data.length);
	}

	public static Float makeFromBeginAndStep(float[] data, double xBegin, double xStep) {
		assert data != null && data.length != 0;
		return new Float(data, xBegin, xBegin + (data.length - 1) * xStep);
	}

	public static Float makeFromBeginAndBack(float[] data, double xBegin, double xBack) {
		assert data != null && data.length != 0;
		return new Float(data, xBegin, xBack);
	}

	public static Float makeFromBeginAndEnd(float[] data, double xBegin, double xEnd) {
		assert data != null && data.length != 0;
		return makeFromBeginAndStep(data, xBegin, (xEnd - xBegin) / data.length);
	}

	protected RegularSeries(Interval2D range, int size) {
		this.range = range;
		this.scale = size != 0 ? size - 1 : 1;
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
		return (int) Math.floor(it.transform(x));
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
		public boolean isEmpty() {
			return yValues.length == 0;
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
		public boolean isEmpty() {
			return yValues.length == 0;
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
