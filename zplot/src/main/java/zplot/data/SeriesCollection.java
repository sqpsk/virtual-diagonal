package zplot.data;

import java.util.ArrayList;
import zplot.plotter.IPlotter;
import zplot.utility.Interval2D;

public class SeriesCollection {

	public static class Entry {

		Entry(Object key, ISeries series, IPlotter plotter) {
			this.key = key;
			this.series = series;
			this.plotter = plotter;
		}
		public final Object key;
		public final ISeries series;
		public final IPlotter plotter;
	}

	SeriesCollection(Interval2D envelope, ArrayList<Entry> data) {
		this.envelope = envelope;
		this.data = data;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public int size() {
		return data.size();
	}

	public Entry get(int i) {
		return data.get(i);
	}

	public Interval2D envelope() {
		return envelope;
	}

	public int sampleCount(int i) {
		return data.get(i).series.size();
	}

	private final ArrayList<Entry> data;
	private final Interval2D envelope;
}
