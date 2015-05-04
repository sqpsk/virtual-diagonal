package siggui.timeseries;

import siggui.api.AbstractSigGuiTask;


class TimeseriesTask extends AbstractSigGuiTask<TimeseriesParameters, ITimeseriesResult> {

    TimeseriesTask(TimeseriesParameters parameters) {
        super(parameters);
    }
    
    @Override
    protected ITimeseriesResult doInBackgroundImpl() {
        return calculateNative(p.getBegin(), p.getEnd(), p.getSeriesType());
    }

    private static native ITimeseriesResult calculateNative(long sampleBegin, long sampleEnd, int seriesMask);
}
