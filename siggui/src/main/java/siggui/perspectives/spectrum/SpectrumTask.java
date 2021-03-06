package siggui.perspectives.spectrum;

import siggui.perspectives.PerspectiveTask;

class SpectrumTask extends PerspectiveTask<SpectrumParameters, SpectrumResult> {

	SpectrumTask(SpectrumParameters parameters) {
		super(parameters);
	}

	@Override
	protected SpectrumResult doInBackgroundImpl() {
		return calculateNative(p.getBegin(), p.getEnd(),
				p.getFftSize(), p.getFftStep(), p.getFftCount(),
				p.getWindow());
	}

	private static native SpectrumResult calculateNative(
			long sampleBegin,
			long sampleEnd,
			int fftSize,
			int fftStep,
			int fftCount,
			int window);
}
