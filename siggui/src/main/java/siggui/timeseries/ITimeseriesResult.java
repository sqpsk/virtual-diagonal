package siggui.timeseries;

import java.util.Map;

interface ITimeseriesResult {
    
    long getBegin();

    long getEnd();

    void update(Map<Integer, float[]> series);
}
