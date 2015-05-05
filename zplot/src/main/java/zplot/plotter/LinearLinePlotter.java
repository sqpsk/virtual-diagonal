package zplot.plotter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import zplot.data.IOrderedSeries;
import zplot.data.ISeries;
import zplot.utility.IntervalTransform;
import zplot.utility.ZMath;

public class LinearLinePlotter extends LinePlotter {

	public LinearLinePlotter(Stroke stroke, Color color) {
		super(stroke, color);
	}

	public LinearLinePlotter(Color color) {
		super(color);
	}

	public LinearLinePlotter() {
	}

	@Override
	public Double interpY(IOrderedSeries series, double x) {
		if (x < series.xRange().begin() || x > series.xRange().end()) {
			return null;
		}
		return interpY(series, series.pullBack(x), x);
	}

	private double interpY(ISeries series, int i, double x) {
		double x0 = series.x(i);
		assert (x0 <= x);
		double y0 = series.y(i);
		if (x == x0) {
			return y0;
		}
		double x1 = series.x(i + 1);
		assert (x1 > x);
		double y1 = series.y(i + 1);
		if (y0 != y1) {
			return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
		} else {
			return y0;
		}
	}

	@Override
	protected void paintLines(
			Graphics2D g, ISeries series, IntervalTransform xt, IntervalTransform yt) {
		int x0 = ZMath.roundPositive(xt.transform(series.x(0)));
		int y0 = ZMath.roundPositive(yt.transform(series.y(0)));
		for (int i = 1; i < series.size(); ++i) {
			int x1 = ZMath.roundPositive(xt.transform(series.x(i)));
			int y1 = ZMath.roundPositive(yt.transform(series.y(i)));
			g.drawLine(x0, y0, x1, y1);
			x0 = x1;
			y0 = y1;
		}
	}
}
