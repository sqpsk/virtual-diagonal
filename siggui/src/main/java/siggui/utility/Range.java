package siggui.utility;

public class Range {

    public Range(long begin, long end) {
	this.begin = begin;
	this.end = end;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 71 * hash + (int) (this.begin ^ (this.begin >>> 32));
	hash = 71 * hash + (int) (this.end ^ (this.end >>> 32));
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	return obj instanceof Range && equals((Range) obj);
    }

    public boolean equals(Range other) {
	return begin == other.begin && end == other.end;
    }

    private final long begin;
    private final long end;
}
