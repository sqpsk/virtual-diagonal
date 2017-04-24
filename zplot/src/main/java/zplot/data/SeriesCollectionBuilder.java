package zplot.data;

import java.util.ArrayList;
import zplot.utility.Interval2D;
import zplot.plotter.Plotter;

/**
 * A builder of {@link SeriesCollection} instances.
 */
public class SeriesCollectionBuilder {

	/**
	 * Builds an empty SeriesCollection with the specified (x,y)-envelope used
	 * to draw the axis without plotting any data.
	 *
	 * @param xBegin The lowest x-axis value.
	 * @param xEnd The highest x-axis value.
	 * @param yBegin The lowest y-axis value.
	 * @param yEnd The highest y-axis value.
	 * @return An empty SeriesCollection instance with the specified envelope.
	 */
	public static SeriesCollection emptyCollection(double xBegin, double xEnd, double yBegin, double yEnd) {
		if (xBegin > xEnd) {
			throw new IllegalArgumentException("xBegin=" + xBegin + ", xEnd=" + xEnd + ", must have xBegin <= xEnd");
		}
		if (yBegin > yEnd) {
			throw new IllegalArgumentException("yBegin=" + yBegin + ", yEnd=" + yEnd + ", must have yBegin <= yEnd");
		}
		return new SeriesCollection(new Interval2D(xBegin, xEnd, yBegin, yEnd), new ArrayList<SeriesCollection.Entry>(0));
	}

	public SeriesCollectionBuilder() {
		data = new ArrayList();
	}

	public SeriesCollectionBuilder(SeriesCollectionBuilder other) {
		data = new ArrayList<SeriesCollection.Entry>(other.data);
	}

	public SeriesCollectionBuilder(SeriesCollection other) {
		data = new ArrayList<SeriesCollection.Entry>(other.size());
		for (int i = 0; i != other.size(); ++i) {
			data.add(other.get(i));
		}
	}

	/**
	 * @return The number of series added.
	 */
	public int size() {
		return data.size();
	}

	/**
	 * @return true if no series have been added.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Remove all series from the builder.
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Add a {@link Series} with associated {@link Plotter} used to plot the
	 * series.
	 */
	public void add(Series series, Plotter plotter) {
		checkArguments(series, plotter);
		add(null, series, plotter);
	}

	/**
	 * Add a {@link Series} with associated {@link Plotter} used to plot the
	 * series, and specify a key to identify the series.
	 */
	public void add(Object key, Series series, Plotter plotter) {
		checkArguments(series, plotter);
		data.add(new SeriesCollection.Entry(key, series, plotter));
	}

	/**
	 * Builds an immutable SeriesCollection instance containing the specified
	 * series.
	 */
	public SeriesCollection build() {
		ArrayList<SeriesCollection.Entry> copy = new ArrayList<SeriesCollection.Entry>(data);
		Interval2D envelope = Interval2D.invalid();
		for (SeriesCollection.Entry e : copy) {
			envelope = Interval2D.envelope(envelope, e.getSeries().range());
		}
		return new SeriesCollection(envelope, copy);
	}

	private void checkArguments(Series series, Plotter plotter) {
		if (series == null) {
			throw new NullPointerException("series argument cannot be null");
		}
		if (plotter == null) {
			throw new NullPointerException("plotter argument cannot be null");
		}
	}

	private final ArrayList<SeriesCollection.Entry> data;
}
