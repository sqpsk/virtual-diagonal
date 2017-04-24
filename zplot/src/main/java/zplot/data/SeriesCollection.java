package zplot.data;

import java.util.ArrayList;
import zplot.utility.Interval2D;
import zplot.plotter.Plotter;

/**
 * Class containing all series to plot. Instances are immutable and created with
 * the {@link SeriesCollectionBuilder} class.
 */
public class SeriesCollection {

	/**
	 * Class binding together a {@link Series} instance with a {@link Plotter}
	 * instance used to plot the series. Also holds an optional key which will
	 * be converted to String used in any legend.
	 */
	public static class Entry {

		Entry(Object key, Series series, Plotter plotter) {
			this.key = key;
			this.series = series;
			this.plotter = plotter;
		}

		public Object getKey() {
			return key;
		}

		public Series getSeries() {
			return series;
		}

		public Plotter getPlotter() {
			return plotter;
		}

		private final Object key;
		private final Series series;
		private final Plotter plotter;
	}

	SeriesCollection(Interval2D envelope, ArrayList<Entry> data) {
		this.envelope = envelope;
		this.data = data;
	}

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Entry get(int i) {
		return data.get(i);
	}

	public Interval2D envelope() {
		return envelope;
	}

	private final ArrayList<Entry> data;
	private final Interval2D envelope;
}
