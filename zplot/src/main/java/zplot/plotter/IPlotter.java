package zplot.plotter;

import zplot.data.ISeries;
import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZGraphics2D;

public interface IPlotter {

	void paintComponent(
			ZGraphics2D g, ISeries series, IntervalTransform xt, IntervalTransform yt, Interval2D canvas);
}
