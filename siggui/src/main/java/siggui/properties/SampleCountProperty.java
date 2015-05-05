package siggui.properties;

public class SampleCountProperty extends TemplateProperty<Long> {

	public SampleCountProperty(Long samples) {
		super("Samples", "", samples);
	}
}
