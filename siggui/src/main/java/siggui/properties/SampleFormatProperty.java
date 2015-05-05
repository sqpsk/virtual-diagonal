package siggui.properties;

public class SampleFormatProperty extends TemplateProperty<SampleFormat> {

	public SampleFormatProperty(SampleFormat fields) {
		super("Sample format", "", fields);
	}

	public SampleFormatProperty(boolean isComplex, int sampleType, int bitsPerSample, boolean isLittleEndian) {
		this(SampleFormat.make(isComplex, sampleType, bitsPerSample, isLittleEndian));
	}
}
