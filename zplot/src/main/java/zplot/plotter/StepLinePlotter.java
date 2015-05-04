package zplot.plotter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import zplot.data.IOrderedSeries;
import zplot.data.ISeries;
import zplot.utility.IntervalTransform;
import zplot.utility.ZMath;

public class StepLinePlotter extends LinePlotter {

    public StepLinePlotter(Stroke stroke, Color color) {
	super(stroke, color);
    }

    public StepLinePlotter(Color color) {
	super(color);
    }

    public StepLinePlotter() {
    }

    @Override
    public Double interpY(IOrderedSeries series, double x) {
	if (x < series.xRange().begin() || x > series.xRange().end()) {
	    return null;
	}
	return series.y(series.pullBack(x));
    }

    @Override
    protected void paintLines(
	    Graphics2D g, ISeries series, IntervalTransform xt, IntervalTransform yt) {
	int x0 = ZMath.roundPositive(xt.transform(series.x(0)));
	int y0 = ZMath.roundPositive(yt.transform(series.y(0)));
	for (int i = 1; i < series.size(); ++i) {
	    int x1 = ZMath.roundPositive(xt.transform(series.x(i)));
	    int y1 = ZMath.roundPositive(yt.transform(series.y(i)));
	    g.drawLine(x0, y0, x1, y0);
	    g.drawLine(x1, y0, x1, y1);
	    x0 = x1;
	    y0 = y1;
	}
    }
}
