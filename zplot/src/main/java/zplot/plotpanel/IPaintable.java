package zplot.plotpanel;

import java.awt.Graphics2D;
import zplot.utility.Interval2D;

public interface IPaintable {

	void paintComponent(Graphics2D g, Interval2D canvas);
}
