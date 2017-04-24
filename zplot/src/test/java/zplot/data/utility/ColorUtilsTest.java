package zplot.data.utility;

import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import zplot.utility.ColorUtils;

public class ColorUtilsTest {

	@Test
	public void getContrastColor_passedBlack_returnsWhite() {
		Assert.assertEquals(Color.BLACK, ColorUtils.getContrastColor(Color.WHITE));
	}

	@Test
	public void getContrastColor_passedWhite_returnsBlack() {
		Assert.assertEquals(Color.WHITE, ColorUtils.getContrastColor(Color.BLACK));
	}
}
