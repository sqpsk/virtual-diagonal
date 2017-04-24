package siggui.utility;

import zplot.utility.Interval;

public class AdaptiveRangeSupport {

	public void reset() {
		begin = 1.0;
		end = 0.0;
	}

	// Only change if diff is large enough / different long enough
	public void newRange(Interval r) {
		if (begin > end) {
			begin = r.begin();
			end = r.end();
		} else {
			double diff = r.begin() - begin;
			if (diff < 0.0) {
				begin = r.begin();
			} else if (diff / r.size() > 0.2) {
				begin += diff / 2.0;
			}

			diff = end - r.end();
			if (diff < 0.0) {
				end = r.end();
			} else if (diff / r.size() > 0.2) {
				end -= diff / 2.0;
			}
		}
	}

	public Interval getAxisRange() {
		return new Interval(begin, end);
	}

	private double begin = 1.0;
	private double end = 0.0;
}
