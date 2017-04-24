package zplot.plotpanel;

import java.awt.Graphics2D;
import zplot.utility.Interval2D;

public interface Paintable {

	void paintComponent(Graphics2D g, Interval2D canvas);
}
