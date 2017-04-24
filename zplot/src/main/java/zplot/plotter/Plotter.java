package zplot.plotter;

import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZGraphics2D;
import zplot.data.Series;

public interface Plotter {

	void paintComponent(
			ZGraphics2D g, Series series, IntervalTransform xt, IntervalTransform yt, Interval2D canvas);
}
