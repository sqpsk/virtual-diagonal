package siggui.perspectives.spectrum;

import siggui.perspectives.PerspectiveParameters;
import siggui.utility.Range;
import siggui.utility.SigMath;

class SpectrumParameters extends PerspectiveParameters {

	public static final int WINDOW_GAUSS = 0;
	public static final int WINDOW_TRIANGLE = 1;
	public static final int WINDOW_RECTANGLE = 2;
	public static final int MIN_FFT_SIZE = 128;
	public static final int MAX_FFT_SIZE = 4 * 1024 * 1024;

	SpectrumParameters(long fileSizeSamples, boolean isComplex, double sampleRateHz) {
		super(fileSizeSamples);
		this.isComplex = isComplex;
		final int logFileSize = SigMath.log2UpperBound(fileSizeSamples) - 1;
		final int fftSizeBound = 1 << logFileSize;
		maxFftSize = Math.min(MAX_FFT_SIZE, fftSizeBound);
		minFftSize = Math.min(MIN_FFT_SIZE, maxFftSize);
		fftSize = Math.min(1024, maxFftSize);
		fftStep = fftSize / 2;
		begin = 0;
		fftCount = Math.min(10, SigMath.maxBlockCount(fftSize, fftStep, (int) fileSizeSamples));
		end = SigMath.coverSize(fftSize, fftStep, fftCount);
		window = WINDOW_GAUSS;
		if (isComplex) {
			plotMinFreq = -sampleRateHz / 2.0;
			plotMaxFreq = sampleRateHz / 2.0;
			plotMinRelativeFreq = -0.5;
			plotMaxRelativeFreq = 0.5;
			plotMinBin = -fftSize / 2;
			plotMaxBin = fftSize / 2;
		} else {
			plotMinFreq = 0;
			plotMaxFreq = sampleRateHz / 2.0;
			plotMinRelativeFreq = 0.0;
			plotMaxRelativeFreq = 0.5;
			plotMinBin = 0;
			plotMaxBin = fftSize / 2;
		}
	}

	@Override
	public SpectrumParameters clone() {
		SpectrumParameters p = new SpectrumParameters(fileSizeSamples, isComplex, plotMaxFreq * 2.0);
		p.window = window;
		p.fftSize = fftSize;
		p.fftStep = fftStep;
		p.fftCount = fftCount;
		p.begin = begin;
		p.end = end;
		p.plotMinBin = plotMinBin;
		p.plotMaxBin = plotMaxBin;
		return p;
	}

	public int getMaxFftSize() {
		return maxFftSize;
	}

	public int getMinFftSize() {
		return minFftSize;
	}

	public double getPlotMinFreq() {
		return plotMinFreq;
	}

	public double getPlotMaxFreq() {
		return plotMaxFreq;
	}

	public int getPlotMinBin() {
		return plotMinBin;
	}

	public int getPlotMaxBin() {
		return plotMaxBin;
	}

	public double getPlotMinRelativeFreq() {
		return plotMinRelativeFreq;
	}

	public double getPlotMaxRelativeFreq() {
		return plotMaxRelativeFreq;
	}

	@Override
	public void setRange(long begin, long end, boolean isAdjusting) {
		Range oldRange = new Range(this.begin, this.end);
		boolean oldIsAdjusting = this.isAdjusting;
		this.begin = begin;
		this.end = end;
		this.isAdjusting = isAdjusting;
		fftCount = SigMath.maxBlockCount(fftSize, fftStep, (int) (end - begin));
		if (oldIsAdjusting == this.isAdjusting) {
			changeSupport.firePropertyChange("range", oldRange, new Range(this.begin, this.end));
		} else {
			changeSupport.firePropertyChange("range", null, new Range(this.begin, this.end));
		}
	}

	public int getFftSize() {
		return fftSize;
	}

	public void setFftSize(int size) {
		assert this.fftSize != size;
		this.fftSize = size;
		this.fftStep = size / 2;
		// Approximatly cover the same range
		fftCount = Math.max(1, SigMath.maxBlockCount(fftSize, fftStep, (int) (end - begin)));
		int rangeSize = SigMath.coverSize(fftSize, fftStep, fftCount);
		end = Math.min(begin + rangeSize, fileSizeSamples);
		begin = end - rangeSize;
		if (isComplex) {
			plotMinBin = -fftSize / 2;
			plotMaxBin = fftSize / 2;
		} else {
			plotMinBin = 0;
			plotMaxBin = fftSize / 2;
		}
		changeSupport.firePropertyChange("range", null, null);
	}

	public int getFftStep() {
		return fftStep;
	}

	public int getFftCount() {
		return fftCount;
	}

	public void setFftCount(int fftCount) {
		this.fftCount = Math.min(fftCount, SigMath.maxBlockCount(fftSize, fftStep, (int) (fileSizeSamples - begin)));
		int rangeSize = SigMath.coverSize(fftSize, fftStep, this.fftCount);
		this.end = Math.min(begin + rangeSize, fileSizeSamples);
		changeSupport.firePropertyChange("range", null, null);
	}

	public int getWindow() {
		return window;
	}

	public void setWindow(int window) {
		int oldWindow = this.window;
		this.window = window;
		changeSupport.firePropertyChange("fftWindow", oldWindow, window);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(begin).append(", ").append(end).append(')');
		sb.append(" fftSize ").append(fftSize);
		sb.append(" fftStep ").append(fftStep);
		sb.append(" fftCount ").append(fftCount);
		sb.append(" window ").append(window);
		return sb.toString();
	}

	@Override
	public boolean isCalculationEquivalent(PerspectiveParameters other) {
		return super.isCalculationEquivalent(other) && isCalculationEquivalent((SpectrumParameters) other);
	}

	public boolean isCalculationEquivalent(SpectrumParameters other) {
		return fftSize == other.fftSize
				&& fftStep == other.fftStep
				&& fftCount == other.fftCount
				&& window == other.window;
	}

	// Per file constants
	private final boolean isComplex;
	private final int maxFftSize;
	private final int minFftSize;
	// Calculation parameters.
	private int fftSize;
	private int fftStep;
	private int fftCount;
	private int window;
	// Display parameters
	private final double plotMinFreq;
	private final double plotMaxFreq;
	private final double plotMinRelativeFreq;
	private final double plotMaxRelativeFreq;
	private int plotMinBin;
	private int plotMaxBin;
}
