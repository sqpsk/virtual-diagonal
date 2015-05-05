package zplot.utility;

public class IntervalTransform {

	public IntervalTransform(double srcBegin, double srcEnd, double dstBegin, double dstEnd) {
		this.srcBegin = srcBegin;
		this.srcEnd = srcEnd;
		this.dstBegin = dstBegin;
		this.dstEnd = dstEnd;
		if (srcEnd != srcBegin) {
			this.useSrcBegin = srcBegin;
			this.useDstBegin = dstBegin;
			this.scale = ((dstEnd - dstBegin)) / (srcEnd - srcBegin);
		} else {
			this.useSrcBegin = 0.0;
			this.useDstBegin = (dstBegin + dstEnd) / 2;
			this.scale = 0.0;
		}
	}

	public IntervalTransform(Interval src, Interval dst) {
		this(src.begin(), src.end(), dst.begin(), dst.end());
	}

	public IntervalTransform(Interval src, double dstBegin, double dstEnd) {
		this(src.begin(), src.end(), dstBegin, dstEnd);
	}

	public IntervalTransform(double srcBegin, double srcEnd, Interval dst) {
		this(srcBegin, srcEnd, dst.begin(), dst.end());
	}

	public double transform(double x) {
		return useDstBegin + (x - useSrcBegin) * scale;
	}

	public Interval transform(Interval x) {
		return new Interval(transform(x.begin()), transform(x.end()));
	}

	public Interval transformFlip(Interval x) {
		return new Interval(transform(x.end()), transform(x.begin()));
	}

	public IntervalTransform inverseTransform() {
		return new IntervalTransform(dstBegin, dstEnd, srcBegin, srcEnd);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(new Interval(srcBegin, srcEnd));
		sb.append(" |--> ");
		sb.append(new Interval(dstBegin, dstEnd));
		return sb.toString();
	}
	private final double srcBegin;
	private final double srcEnd;
	private final double dstBegin;
	private final double dstEnd;
	private final double useSrcBegin;
	private final double useDstBegin;
	private final double scale;
}
