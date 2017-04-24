package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;

/**
 * An interface representing a plottable data series. Used to access the x and y
 * values of a sequence of 2D-points.
 */
public interface Series {

	/**
	 * @return the number of points in the series
	 */
	int size();

	/**
	 * @param i index of the point
	 * @return the x coordinate of the i'th point
	 */
	double x(int i);

	/**
	 * @param i index of the point.
	 * @return the y coordinate of the i'th point
	 */
	double y(int i);

	/**
	 * @return an Interval object representing the minimum and maximum x-values.
	 */
	Interval xRange();

	/**
	 * @return an Interval object representing the minimum and maximum y-values.
	 */
	Interval yRange();

	/**
	 * @return an Interval2D object representing the minimum and maximum x and y
	 * values.
	 */
	Interval2D range();
}
