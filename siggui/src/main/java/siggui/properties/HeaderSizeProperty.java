package siggui.properties;

public class HeaderSizeProperty extends TemplateProperty<Long> {

	public HeaderSizeProperty(Long bytes) {
		super("Header size", "B", bytes);
	}
}
