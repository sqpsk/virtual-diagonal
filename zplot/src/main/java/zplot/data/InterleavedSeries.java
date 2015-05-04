package zplot.data;

import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.ZMath;

/**
 * Represents irregularly sampled time-series data, or x-y scatter data.
 *
 */
public abstract class InterleavedSeries implements ISeries {

    public static InterleavedSeries.Double makeScatterSeries(double[] interleaved) {
        assert (interleaved.length & 1) == 0 : "interleaved must have even length";
        return new InterleavedSeries.Double(interleaved);
    }

    public static InterleavedSeries.Float makeScatterSeries(float[] interleaved) {
        assert (interleaved.length & 1) == 0 : "interleaved must have even length";
        return new InterleavedSeries.Float(interleaved);
    }
    
    public static InterleavedSeries.Double makeOrderedSeries(double[] interleaved) {
        assert (interleaved.length & 1) == 0 : "interleaved must have even length";
        return new InterleavedSeries.OrderedDouble(interleaved);
    }

    public static InterleavedSeries.Float makeOrderedSeries(float[] interleaved) {
        assert (interleaved.length & 1) == 0 : "interleaved must have even length";
        return new InterleavedSeries.OrderedFloat(interleaved);
    }

    public InterleavedSeries(Interval2D range) {
        this.range = range;
    }

    @Override
    public Interval2D range() {
        return range;
    }

    @Override
    public Interval xRange() {
        return range.x();
    }

    @Override
    public Interval yRange() {
        return range.y();
    }

    public int pullBack(double x) {
        for (int i = 0; i != size(); ++i) {
            double xx = x(i);
            if (xx == x) {
                return i;
            } else if (xx > x) {
                return i - 1;
            }
        }
        return 0;
    }
    
    private final Interval2D range;

    public static class Double extends InterleavedSeries {

        private Double(double[] interleaved) {
            super(ZMath.calculateInterleavedRange(interleaved, 0, interleaved.length));
            this.interleaved = interleaved;
        }

        @Override
        public int size() {
            return interleaved.length / 2;
        }

        @Override
        public boolean isEmpty() {
            return interleaved.length == 0;
        }

        @Override
        public double x(int i) {
            return interleaved[2 * i];
        }

        @Override
        public double y(int i) {
            return interleaved[2 * i + 1];
        }
        // Data stored in (x, y) interleaved format.
        private final double[] interleaved;
    }

    public static class OrderedDouble extends InterleavedSeries.Double implements IOrderedSeries {

        private OrderedDouble(double[] interleaved) {
            super(interleaved);
        }
    }

    public static class Float extends InterleavedSeries {

        private Float(float[] interleaved) {
            super(ZMath.calculateInterleavedRange(interleaved, 0, interleaved.length));
            this.interleaved = interleaved;
        }

        @Override
        public int size() {
            return interleaved.length / 2;
        }

        @Override
        public boolean isEmpty() {
            return interleaved.length == 0;
        }

        @Override
        public double x(int i) {
            return interleaved[2 * i];
        }

        @Override
        public double y(int i) {
            return interleaved[2 * i + 1];
        }
        // Data stored in (x, y) interleaved format.
        private final float[] interleaved;
    }

    public static class OrderedFloat extends InterleavedSeries.Float implements IOrderedSeries {

        private OrderedFloat(float[] interleaved) {
            super(interleaved);
        }
    }
}
