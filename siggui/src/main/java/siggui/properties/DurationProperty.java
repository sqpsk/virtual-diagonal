package siggui.properties;

import java.text.DecimalFormat;
import zplot.utility.SiUnits;

public class DurationProperty extends TemplateProperty<Double> {

	public DurationProperty(Double durationSeconds) {
		super("Duration", "s", durationSeconds);
	}

	@Override
	public String displayValue() {
		return SiUnits.toSiString(value, units, df);
	}

	private static final DecimalFormat df = new DecimalFormat("#.###");
}
