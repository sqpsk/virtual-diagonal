package zplot.data;

import org.junit.Test;

public class InterleavedSeriesTest {

	@Test(expected = IllegalArgumentException.class)
	public void makeOrderedSeries_passedOddLengthDoubleArray_throwsException() {
		InterleavedSeries.makeOrderedSeries(new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void makeScatterSeries_passedOddLengthDoubleArray_throwsException() {
		InterleavedSeries.makeScatterSeries(new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void makeOrderedSeries_passedOddLengthFloatArray_throwsException() {
		InterleavedSeries.makeOrderedSeries(new float[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void makeScatterSeries_passedOddLengthFloatArray_throwsException() {
		InterleavedSeries.makeScatterSeries(new float[1]);
	}
}
