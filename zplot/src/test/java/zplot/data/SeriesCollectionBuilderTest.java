package zplot.data;

import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import zplot.plotter.LinearLinePlotter;
import zplot.plotter.Plotter;

public class SeriesCollectionBuilderTest {

	@Test(expected = NullPointerException.class)
	public void add_passedNullSeries_throwsException() {
		SeriesCollectionBuilder b = new SeriesCollectionBuilder();
		b.add(null, new LinearLinePlotter(Color.BLUE));
	}

	@Test(expected = NullPointerException.class)
	public void add_passedNullPlotter_throwsException() {
		SeriesCollectionBuilder b = new SeriesCollectionBuilder();
		b.add(InterleavedSeries.makeOrderedSeries(new float[2]), null);
	}

	@Test
	public void seriesReturnedInInsertionOrder() {
		SeriesCollectionBuilder b = new SeriesCollectionBuilder();

		Series series0 = InterleavedSeries.makeOrderedSeries(new float[2]);
		Plotter plotter0 = new LinearLinePlotter(Color.BLUE);

		Series series1 = InterleavedSeries.makeOrderedSeries(new float[2]);
		Plotter plotter1 = new LinearLinePlotter(Color.BLUE);

		b.add(series0, plotter0);
		b.add(series1, plotter1);
		SeriesCollection seriesCollection = b.build();

		SeriesCollection.Entry e0 = seriesCollection.get(0);
		SeriesCollection.Entry e1 = seriesCollection.get(1);

		Assert.assertSame(series0, e0.getSeries());
		Assert.assertSame(plotter0, e0.getPlotter());
		Assert.assertSame(series1, e1.getSeries());
		Assert.assertSame(plotter1, e1.getPlotter());
	}
}
