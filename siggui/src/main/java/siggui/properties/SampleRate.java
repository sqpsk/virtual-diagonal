package siggui.properties;

import java.text.DecimalFormat;
import zplot.utility.SiUnits;

public class SampleRate extends TemplateProperty<Long> {

	public SampleRate(Long value) {
		super("Sample rate", "Hz", value);
	}

	@Override
	public String displayValue() {
		return SiUnits.toSiString(value, units, df);
	}

	private static DecimalFormat df = new DecimalFormat("#.###");
}
