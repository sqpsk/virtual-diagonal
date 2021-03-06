package zplot.utility;

public class Interval {

	public static Interval invalid() {
		return new Interval(Double.MAX_VALUE, -Double.MAX_VALUE);
	}

	public static Interval envelope(Interval a, Interval b) {
		return new Interval(Math.min(a.begin, b.begin), Math.max(a.end, b.end));
	}

	public static Interval intersection(Interval a, Interval b) {
		return new Interval(Math.max(a.begin, b.begin), Math.min(a.end, b.end));
	}

	public Interval(double begin, double end) {
		this.begin = begin;
		this.end = end;
	}

	public double begin() {
		return begin;
	}

	public double end() {
		return end;
	}

	public double size() {
		return end - begin;
	}

	public double mid() {
		return (begin + end) / 2.0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(begin).append(", ").append(end).append(')');
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.begin) ^ (Double.doubleToLongBits(this.begin) >>> 32));
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.end) ^ (Double.doubleToLongBits(this.end) >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Interval && equals((Interval) obj);
	}

	public boolean equals(Interval other) {
		return begin == other.begin && end == other.end;
	}
	
	private final double begin;
	private final double end;
}
