package siggui.perspectives.spectrum;

import java.beans.PropertyChangeEvent;
import siggui.perspectives.AbstractPerspectiveController;
import siggui.perspectives.PerspectiveTask;
import siggui.properties.PropertySet;
import siggui.properties.SampleCountProperty;
import siggui.properties.SampleFormat;
import siggui.properties.SampleFormatProperty;
import siggui.properties.SampleRate;

class SpectrumController extends AbstractPerspectiveController<SpectrumParameters, SpectrumResult> {

	@Override
	protected SpectrumParameters newParameters(PropertySet properties) {
		long sampleCount = properties.get(SampleCountProperty.class).value();
		SampleFormat sampleFormat = properties.get(SampleFormatProperty.class).value();
		long sampleRateHz = properties.get(SampleRate.class).value();
		return new SpectrumParameters(sampleCount, sampleFormat.isComplex, sampleRateHz);
	}

	@Override
	protected PerspectiveTask<SpectrumParameters, SpectrumResult> newTask(SpectrumParameters parameters) {
		return new SpectrumTask(parameters);
	}

	@Override
	protected void notifyParametersChanged(PropertyChangeEvent pce) {
		calculate(false);
	}
}
