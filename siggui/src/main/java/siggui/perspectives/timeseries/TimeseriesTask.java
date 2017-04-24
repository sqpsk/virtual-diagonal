package siggui.perspectives.timeseries;

import siggui.perspectives.PerspectiveTask;

class TimeseriesTask extends PerspectiveTask<TimeseriesParameters, TimeseriesResult> {

	TimeseriesTask(TimeseriesParameters parameters) {
		super(parameters);
	}

	@Override
	protected TimeseriesResult doInBackgroundImpl() {
		return calculateNative(p.getBegin(), p.getEnd(), p.getSeriesType());
	}

	private static native TimeseriesResult calculateNative(long sampleBegin, long sampleEnd, int seriesMask);
}
