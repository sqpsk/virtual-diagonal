package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;

public interface ISeries {

	int size();

	boolean isEmpty();

	double x(int i);

	double y(int i);

	Interval2D range();

	Interval xRange();

	Interval yRange();
}
