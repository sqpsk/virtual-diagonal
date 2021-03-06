package zplot.data;

import org.junit.Assert;
import org.junit.Test;
import zplot.utility.Interval;
import zplot.utility.Interval2D;

public abstract class OnePointSeriesTest {

	private static final int x0 = -1;
	private static final int y0 = -10;

	public static class InterleavedSeriesDoubleTest extends OnePointSeriesTest {

		public InterleavedSeriesDoubleTest() {
			super(InterleavedSeries.makeOrderedSeries(new double[]{x0, y0}));
		}
	}

	public static class InterleavedSeriesFloatTest extends OnePointSeriesTest {

		public InterleavedSeriesFloatTest() {
			super(InterleavedSeries.makeOrderedSeries(new float[]{x0, y0}));
		}
	}

	public static class RegularSeriesDoubleTest extends OnePointSeriesTest {

		public RegularSeriesDoubleTest() {
			super(RegularSeries.makeFromBeginAndBack(new double[]{y0}, x0, x0));
		}
	}

	public static class RegularSeriesFloatTest extends OnePointSeriesTest {

		public RegularSeriesFloatTest() {
			super(RegularSeries.makeFromBeginAndBack(new float[]{y0}, x0, x0));
		}
	}

	@Test
	public void seriesInterfaceMethods_areCorrect() {
		Assert.assertEquals(1, series.size());
		Assert.assertEquals(new Interval(x0, x0), series.xRange());
		Assert.assertEquals(new Interval(y0, y0), series.yRange());
		Assert.assertEquals(new Interval2D(x0, x0, y0, y0), series.range());
		Assert.assertEquals(x0, series.x(0), 0.0);
		Assert.assertEquals(y0, series.y(0), 0.0);
	}

	@Test
	public void pullBack_isCorrect() {
		OrderedSeries orderedSeries = (OrderedSeries) series;
		Assert.assertEquals(0, orderedSeries.pullBack(-100));
		Assert.assertEquals(0, orderedSeries.pullBack(x0));
		Assert.assertEquals(0, orderedSeries.pullBack(100));
	}

	private OnePointSeriesTest(Series series) {
		this.series = series;
	}

	protected final Series series;
}
