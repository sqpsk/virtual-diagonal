package zplot.plotter;

import java.awt.Color;
import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZGraphics2D;
import zplot.utility.ZMath;
import zplot.data.Series;

public class DotPlotter implements Plotter {

	public DotPlotter(Color color, int size) {
		this.color = color;
		this.size = 1 + 2 * (size / 2);
		this.hSize = size / 2;
	}

	public DotPlotter(Color color) {
		this(color, 3);
	}

	public DotPlotter() {
		this(Color.BLUE, 3);
	}

	public int getSize() {
		return size;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void paintComponent(
			ZGraphics2D g, Series series, IntervalTransform xt, IntervalTransform yt, Interval2D canvas) {
		g.setColor(color);
		if (size == 3) {
			paintDots3x3(g, series, xt, yt, canvas);
		} else {
			paintDots(g, series, xt, yt, canvas);
		}
	}

	private void paintDots3x3(
			ZGraphics2D g,
			Series series,
			IntervalTransform xt,
			IntervalTransform yt,
			Interval2D canvas) {
		for (int i = 0; i < series.size(); ++i) {
			int x = ZMath.roundPositive(xt.transform(series.x(i)));
			int y = ZMath.roundPositive(yt.transform(series.y(i)));
			if (ZMath.closedContains(canvas, x, y + 1)) {
				g.fillRect3x3(x - 1, y - 1);
			}
		}
	}

	private void paintDots(
			ZGraphics2D g,
			Series series,
			IntervalTransform xt,
			IntervalTransform yt,
			Interval2D canvas) {
		for (int i = 0; i < series.size(); ++i) {
			int x = ZMath.roundPositive(xt.transform(series.x(i))) - hSize;
			int y = ZMath.roundPositive(yt.transform(series.y(i))) - hSize;
			g.fillRect(x, y, size, size);
		}
	}
	private final int size;
	private final int hSize;
	private final Color color;
}
