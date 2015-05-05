package siggui.properties;

public class FileSizeProperty extends TemplateProperty<Long> {

	public FileSizeProperty(Long sizeBytes) {
		super("File size", "B", sizeBytes);
	}
}
