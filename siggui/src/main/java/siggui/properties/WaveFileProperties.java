package siggui.properties;

public class WaveFileProperties {

	public boolean isValid() {
		return sampleFormat != null && error != null;
	}

	public String getError() {
		return error;
	}

	public long getSampleRateHz() {
		return sampleRateHz;
	}

	public SampleFormat getSampleFormat() {
		return sampleFormat;
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public long getHeaderSizeBytes() {
		return headerSizeBytes;
	}

	public WaveFileProperties(
			SampleFormat sampleFormat,
			long headerSizeBytes,
			long sampleCount,
			long sampleRateHz) {
		this.sampleFormat = sampleFormat;
		this.headerSizeBytes = headerSizeBytes;
		this.sampleCount = sampleCount;
		this.sampleRateHz = sampleRateHz;
	}

	private final SampleFormat sampleFormat;
	private final long headerSizeBytes;
	private final long sampleCount;
	private final long sampleRateHz;
	private final String error = null;
}
