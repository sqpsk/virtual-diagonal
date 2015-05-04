package siggui.timeseries;

import siggui.api.AbstractSigGuiParameters;
import siggui.utility.Range;

class TimeseriesParameters extends AbstractSigGuiParameters {

    public static final int MIN_REGION_SIZE = 2;
    public static final int MAX_REGION_SIZE = 1 * 1000 * 1000;
    public static final int SERIES_POWER = 0;
    public static final int SERIES_PHASE = 1;
    public static final int SERIES_I = 2;
    public static final int SERIES_Q = 3;
    public static final int SERIES_IvQ = 4;
    public static final int SERIES_I_AND_Q = 5;
    public static final int PLOT_LINE = 0;
    public static final int PLOT_DOTS = 1;

    public static TimeseriesParameters make(
            long fileSizeSamples,
            double sampleRateHz,
            boolean isComplex,
            TimeseriesParameters other) {
        boolean useOtherSeries =
                other != null
                && (isComplex || other.getSeriesType() == SERIES_POWER || other.getSeriesType() == SERIES_I);
        int seriesType = useOtherSeries ? other.getSeriesType() : SERIES_POWER;
        int plotType = other != null ? other.getPlotType() : PLOT_LINE;
        return new TimeseriesParameters(fileSizeSamples, sampleRateHz, isComplex, seriesType, plotType);
    }

    TimeseriesParameters(
            long fileSizeSamples,
            double sampleRateHz,
            boolean isComplex,
            int seriesType,
            int plotType) {
        super(fileSizeSamples);
        this.sampleRateHz = sampleRateHz;
        this.isComplex = isComplex;
        begin = 0;
        end = Math.min(fileSizeSamples, 1000);
        this.seriesType = seriesType;
        this.plotType = plotType;
    }

    @Override
    public AbstractSigGuiParameters clone() {
        TimeseriesParameters p = new TimeseriesParameters(fileSizeSamples, sampleRateHz, isComplex, seriesType, plotType);
        p.setRange(begin, end, false);
        return p;
    }

    public double getSampleRateHz() {
        return sampleRateHz;
    }

    public boolean isComplex() {
        return isComplex;
    }

    public int getSeriesType() {
        return seriesType;
    }

    public int getPlotType() {
        return plotType;
    }

    public double getPlotMinSamples() {
        return begin;
    }

    public double getPlotMaxSamples() {
        return end - 1;
    }

    public double getPlotMinSeconds() {
        return begin / sampleRateHz;
    }

    public double getPlotMaxSeconds() {
        return (end - 1) / sampleRateHz;
    }

    @Override
    public void setRange(long begin, long end, boolean isAdjusting) {
        Range oldRange = isAdjusting ? new Range(this.begin, this.end) : null;
        this.begin = begin;
        this.end = end;
        this.isAdjusting = isAdjusting;
        changeSupport.firePropertyChange("range", oldRange, new Range(this.begin, this.end));
    }

    public void setSeriesType(int newValue) {
        int oldValue = this.seriesType;
        this.seriesType = newValue;
        changeSupport.firePropertyChange("series", oldValue, newValue);
    }

    public void setPlotType(int newValue) {
        int oldValue = this.plotType;
        plotType = newValue;
        changeSupport.firePropertyChange("plot", oldValue, newValue);
    }

    @Override
    public boolean isCalculationEquivalent(AbstractSigGuiParameters other) {
        return super.isCalculationEquivalent(other) && isCalculationEquivalent((TimeseriesParameters) other);
    }

    public boolean isCalculationEquivalent(TimeseriesParameters other) {
        return seriesType == other.seriesType;
    }
    private final double sampleRateHz;
    private final boolean isComplex;
    private int seriesType;
    private int plotType;
}
