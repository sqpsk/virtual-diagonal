package zplot.plotter;

import zplot.data.OrderedSeries;

public interface InterpolatingPlotter extends Plotter {

	Double interpY(OrderedSeries series, double x);
}
