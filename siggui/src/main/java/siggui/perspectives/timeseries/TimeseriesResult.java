package siggui.perspectives.timeseries;

import java.util.Map;

interface TimeseriesResult {

	long getBegin();

	long getEnd();

	void update(Map<Integer, float[]> series);
}
