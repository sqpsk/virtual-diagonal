package siggui.timeseries;

import java.util.Map;

class TwoSeriesTimeseriesResult implements ITimeseriesResult {

	@Override
	public void update(Map<Integer, float[]> series) {
		series.put(key, data);
		if (data1 != null) {
			series.put(key1, data1);
		}
	}

	@Override
	public long getEnd() {
		return end;
	}

	@Override
	public long getBegin() {
		return begin;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TwoSeriesTimeseriesResult{");
		sb.append("key=").append(key);
		if (data1 != null) {
			sb.append(",").append(key1);
		}
		sb.append(" range=[").append(begin).append(", ").append(end);
		sb.append(") size=").append(end - begin).append("}");
		return sb.toString();
	}
	private float[] data;
	private int key;
	private float[] data1;
	private int key1;
	private long begin;
	private long end;
}
