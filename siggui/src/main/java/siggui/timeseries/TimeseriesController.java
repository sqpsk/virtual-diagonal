package siggui.timeseries;

import java.beans.PropertyChangeEvent;
import java.io.File;
import siggui.api.AbstractSigGuiController;
import siggui.properties.PropertySet;
import siggui.properties.SampleCountProperty;
import siggui.properties.SampleFormat;
import siggui.properties.SampleFormatProperty;
import siggui.properties.SampleRate;

class TimeseriesController extends AbstractSigGuiController<TimeseriesParameters, ITimeseriesResult> {

	@Override
	public void setFile(File file, PropertySet properties) {
		super.setFile(file, properties);
		resultCache = new TimeseriesResultCache();
	}

	@Override
	protected void setResult(TimeseriesParameters p, ITimeseriesResult result) {
		if (p.isCalculationEquivalent(parameters)) {
			resultCache.update(result);
			changeSupport.firePropertyChange("result", null, resultCache);
		}
	}

	@Override
	protected TimeseriesParameters newParameters(PropertySet properties) {
		SampleFormat sampleFormat = properties.get(SampleFormatProperty.class).value();
		long sampleRateHz = properties.get(SampleRate.class).value();
		return TimeseriesParameters.make(
				properties.get(SampleCountProperty.class).value(),
				sampleRateHz,
				sampleFormat.isComplex,
				getParameters());
	}

	@Override
	protected TimeseriesTask newTask(TimeseriesParameters parameters) {
		return new TimeseriesTask(parameters);
	}

	@Override
	protected void notifyParametersChanged(PropertyChangeEvent pce) {
		if ("range".equals(pce.getPropertyName())) {
			resultCache = new TimeseriesResultCache();
			calculate(getParameters().isAdjusting());
		} else if ("series".equals(pce.getPropertyName())) {
			if (!resultCache.contains(getParameters().getSeriesType())) {
				calculate(false);
			} else {
				changeSupport.firePropertyChange("result", null, resultCache);
			}
		} else if ("plot".equals(pce.getPropertyName())) {
			changeSupport.firePropertyChange("result", null, resultCache);
		}
	}
	private TimeseriesResultCache resultCache;
}
