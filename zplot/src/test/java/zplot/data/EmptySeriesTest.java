package zplot.data;

import org.junit.Test;

public class EmptySeriesTest {

	@Test(expected = IllegalArgumentException.class)
	public void makeOrderedSeries_passedEmptyDoubleArray_throwsException() {
		InterleavedSeries.makeOrderedSeries(new double[]{});
	}

	@Test(expected = IllegalArgumentException.class)
	public void makeOrderedSeries_passedEmptyFloatArray_throwsException() {
		InterleavedSeries.makeOrderedSeries(new float[]{});
	}

	@Test(expected = IllegalArgumentException.class)
	public void regularSeries_passedEmptyDoubleArray_throwsException() {
		RegularSeries.makeFromBeginAndBack(new double[]{}, -1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void regularSeries_passedEmptyFloatArray_throwsException() {
		RegularSeries.makeFromBeginAndBack(new float[]{}, -1, 1);
	}
}
