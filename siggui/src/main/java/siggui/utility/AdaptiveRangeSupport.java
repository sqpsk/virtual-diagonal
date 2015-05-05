package siggui.utility;

import zplot.utility.Interval;

public class AdaptiveRangeSupport {

	public void reset() {
		range = null;
	}

	// Only change if diff is large enough / different long enough
	public void newRange(Interval r) {
		if (range == null) {
			range = r;
		} else {
			double diff = r.begin() - range.begin();
			if (diff < 0.0) {
				range.setBegin(r.begin());
			} else if (diff / r.size() > 0.2) {
				range.setBegin(range.begin() + diff / 2.0);
			}

			diff = range.end() - r.end();
			if (diff < 0.0) {
				range.setEnd(r.end());
			} else if (diff / r.size() > 0.2) {
				range.setEnd(range.end() - diff / 2.0);
			}
		}
	}

	public Interval getAxisRange() {
		return range;
	}

	private Interval range = null;
}
