package zplot.plotter;

import zplot.data.IOrderedSeries;

public interface IInterpolatingPlotter extends IPlotter {

	Double interpY(IOrderedSeries series, double x);
}
