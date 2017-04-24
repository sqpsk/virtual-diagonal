package zplot.data;

import org.junit.Assert;
import org.junit.Test;
import zplot.utility.Interval;
import zplot.utility.Interval2D;

public abstract class TwoPointSeriesTest {

	private static final int x0 = -1;
	private static final int y0 = -10;
	private static final int x1 = 1;
	private static final int y1 = 10;

	public static class InterleavedSeriesDoubleTest extends TwoPointSeriesTest {

		public InterleavedSeriesDoubleTest() {
			super(InterleavedSeries.makeOrderedSeries(new double[]{x0, y0, x1, y1}));
		}
	}

	public static class InterleavedSeriesFloatTest extends TwoPointSeriesTest {

		public InterleavedSeriesFloatTest() {
			super(InterleavedSeries.makeOrderedSeries(new float[]{x0, y0, x1, y1}));
		}
	}

	public static class RegularSeriesDoubleTest extends TwoPointSeriesTest {

		public RegularSeriesDoubleTest() {
			super(RegularSeries.makeFromBeginAndBack(new double[]{y0, y1}, x0, x1));
		}
	}

	public static class RegularSeriesFloatTest extends TwoPointSeriesTest {

		public RegularSeriesFloatTest() {
			super(RegularSeries.makeFromBeginAndBack(new float[]{y0, y1}, x0, x1));
		}
	}

	@Test
	public void seriesInterfaceMethods_areCorrect() {
		Assert.assertEquals(2, series.size());
		Assert.assertEquals(new Interval(x0, x1), series.xRange());
		Assert.assertEquals(new Interval(y0, y1), series.yRange());
		Assert.assertEquals(new Interval2D(x0, x1, y0, y1), series.range());
		Assert.assertEquals(x0, series.x(0), 0.0);
		Assert.assertEquals(y0, series.y(0), 0.0);
		Assert.assertEquals(x1, series.x(1), 0.0);
		Assert.assertEquals(y1, series.y(1), 0.0);
	}

	@Test
	public void pullBack_isCorrect() {
		OrderedSeries orderedSeries = (OrderedSeries) series;
		Assert.assertEquals(0, orderedSeries.pullBack(-100));
		Assert.assertEquals(0, orderedSeries.pullBack(x0));
		Assert.assertEquals(1, orderedSeries.pullBack(x1));
		Assert.assertEquals(1, orderedSeries.pullBack(100));
	}

	private TwoPointSeriesTest(Series series) {
		this.series = series;
	}

	protected final Series series;
}
