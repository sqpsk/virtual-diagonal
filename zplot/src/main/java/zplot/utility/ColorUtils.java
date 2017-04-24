package zplot.utility;

import java.awt.Color;

public class ColorUtils {

	// See http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	public static Color getContrastColor(Color c) {
		double luminance = 1 - (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255;
		if (luminance < 0.5) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

}
