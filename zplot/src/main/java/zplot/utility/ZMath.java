package zplot.utility;

public class ZMath {

    public static boolean closedContains(Interval i, double x) {
        return i.begin() <= x && x <= i.end();
    }

    public static boolean closedContains(Interval2D i, double x, double y) {
        return closedContains(i.x(), x) && closedContains(i.y(), y);
    }

    public static int roundPositive(double x) {
        return (int) (x + 0.5);
    }

    public static double bound(double begin, double x, double end) {
        assert begin <= end;
        return Math.min(Math.max(begin, x), end);
    }

    public static long bound(long begin, long x, long end) {
        assert begin <= end;
        return Math.min(Math.max(begin, x), end);
    }

    public static int bound(int begin, int x, int end) {
        assert begin <= end;
        return Math.min(Math.max(begin, x), end);
    }

    public static Interval calculateRange(float[] data, int begin, int end) {
        float min = java.lang.Float.MAX_VALUE;
        float max = -java.lang.Float.MAX_VALUE;
        for (int i = begin; i != end; ++i) {
            float x = data[i];
            min = Math.min(min, x);
            max = Math.max(max, x);
        }
        return new Interval(min, max);
    }
    
    public static Interval calculateRange(double[] data, int begin, int end) {
        double min = java.lang.Double.MAX_VALUE;
        double max = -java.lang.Double.MAX_VALUE;
        for (int i = begin; i != end; ++i) {
            double x = data[i];
            min = Math.min(min, x);
            max = Math.max(max, x);
        }
        return new Interval(min, max);
    }

    // begin, end measured in complex samples
    public static Interval2D calculateInterleavedRange(float[] interleaved, int begin, int end) {
        float xMin = java.lang.Float.MAX_VALUE;
        float xMax = -java.lang.Float.MAX_VALUE;
        float yMin = java.lang.Float.MAX_VALUE;
        float yMax = -java.lang.Float.MAX_VALUE;
        for (int i = begin; i < end; i += 2) {
            float x = interleaved[i];
            float y = interleaved[i + 1];
            xMin = Math.min(xMin, x);
            xMax = Math.max(xMax, x);
            yMin = Math.min(yMin, y);
            yMax = Math.max(yMax, y);
        }
        return new Interval2D(xMin, xMax, yMin, yMax);
    }
    
    public static Interval2D calculateInterleavedRange(double[] interleaved, int begin, int end) {
        double xMin = java.lang.Double.MAX_VALUE;
        double xMax = -java.lang.Double.MAX_VALUE;
        double yMin = java.lang.Double.MAX_VALUE;
        double yMax = -java.lang.Double.MAX_VALUE;
        for (int i = begin; i < end; i += 2) {
            double x = interleaved[i];
            double y = interleaved[i + 1];
            xMin = Math.min(xMin, x);
            xMax = Math.max(xMax, x);
            yMin = Math.min(yMin, y);
            yMax = Math.max(yMax, y);
        }
        return new Interval2D(xMin, xMax, yMin, yMax);
    }
}
