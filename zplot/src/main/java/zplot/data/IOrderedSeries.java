package zplot.data;

/**
 * A series where the x coordinate values are increasing. Every regular
 * RegularSeries implements this interface. An InterleavedSeries can optionally
 * implement it.
 */
public interface IOrderedSeries extends ISeries {

	/**
	 * @return the maximum index i such that x(i) <= x
	 */
	int pullBack(double x);
}
