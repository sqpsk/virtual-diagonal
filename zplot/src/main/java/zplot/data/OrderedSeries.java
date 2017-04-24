package zplot.data;

/**
 * A series where the x coordinate values are increasing (ie x(i) <= x(i + 1)).
 * 
 * Every {@link RegularSeries} implements this interface. An {@link
 * InterleavedSeries} can optionally implement it.
 */
public interface OrderedSeries extends Series {

	/**
	 * @param x
	 * @return the maximum index i such that x(i) <= x. Return 0 if no such
	 * index exists (ie if x < x(0)).
	 */
	int pullBack(double x);
}
