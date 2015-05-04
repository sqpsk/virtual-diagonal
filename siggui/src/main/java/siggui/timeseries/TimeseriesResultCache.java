package siggui.timeseries;

import java.util.HashMap;

class TimeseriesResultCache {

    public float[] getSeries(int key) {
        return series.get(key);
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public boolean contains(int key) {
        if (key != TimeseriesParameters.SERIES_I_AND_Q) {
            return series.containsKey(key);
        } else {
            return series.containsKey(TimeseriesParameters.SERIES_I)
                && series.containsKey(TimeseriesParameters.SERIES_Q);
        }
    }

    public void update(ITimeseriesResult r) {
        r.update(series);
        begin = r.getBegin();
        end = r.getEnd();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(": range ").append(begin).append(", ").append(end);
        return sb.toString();
    }
    private final HashMap<Integer, float[]> series = new HashMap<Integer, float[]>();
    private long begin;
    private long end;
}
