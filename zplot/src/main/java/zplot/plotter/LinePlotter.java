package zplot.plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import zplot.data.ISeries;
import zplot.data.RegularSeries;
import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.IntervalTransform;
import zplot.utility.ZGraphics2D;
import zplot.utility.ZMath;

public abstract class LinePlotter implements IInterpolatingPlotter {

	public LinePlotter(Stroke stroke, Color color) {
		this.stroke = stroke;
		this.color = color;
	}

	public LinePlotter(Color color) {
		this(new BasicStroke(1), color);
	}

	public LinePlotter() {
		this(new BasicStroke(1), Color.BLUE);
	}

	public Stroke getStroke() {
		return stroke;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void paintComponent(
			ZGraphics2D g, ISeries series, IntervalTransform xt, IntervalTransform yt, Interval2D canvas) {
		g.setStroke(stroke);
		g.setColor(color);
		if (series instanceof RegularSeries.Float) {
			IntervalTransform canvasToDataX = xt.inverseTransform();
			double dataBegin = canvasToDataX.transform(canvas.x().begin());
			double dataEnd = canvasToDataX.transform(canvas.x().end());

			int dataIndexBegin = toIndex(dataBegin, series.xRange(), series.size());
			int dataIndexEnd = toIndex(dataEnd, series.xRange(), series.size());
			int dataSize = dataIndexEnd - dataIndexBegin;

			if (dataSize >= 4 * canvas.width()) {
				float[] data = ((RegularSeries.Float) series).yValues();
				paintSaturatedLines(g, data, dataIndexBegin, dataIndexEnd,
						(int) canvas.x().begin(), (int) canvas.x().end(),
						(int) canvas.y().begin(), (int) canvas.y().end(),
						yt);
			} else {
				paintLines(g, series, xt, yt);
			}
		} else {
			paintLines(g, series, xt, yt);
		}
	}

	protected abstract void paintLines(
			Graphics2D g, ISeries series, IntervalTransform xt, IntervalTransform yt);

	private static int toIndex(double x, Interval range, int size) {
		return (int) ((x - range.begin()) * size / range.size());
	}

	// Let r < 1, and p in N. Consider round(pr) = floor(pr + 0.5) = i.
	// Gives ceil((i - 0.5)/r) <= p < celi((i + 1 - 0.5) / r).
	private static void paintSaturatedLines(
			ZGraphics2D g,
			float[] data,
			int dataBegin,
			int dataEnd,
			int canvasBeginX,
			int canvasEndX,
			int canvasBeginY,
			int canvasEndY,
			IntervalTransform yt) {
		final int dataSize = dataEnd - dataBegin;
		final int canvasWidth = canvasEndX - canvasBeginX;
		final double r = (double) canvasWidth / dataSize;
		float y = data[dataBegin];
		for (int i = 0; i != canvasWidth; ++i) {
			int j0 = dataBegin + Math.max(0, (int) ((i - 0.5) / r));
			int j1 = dataBegin + Math.min(dataSize, (int) ((i + 0.5) / r));
			float yMin = y;
			float yMax = y;
			for (int j = j0; j != j1; ++j) {
				y = data[j];
				yMin = Math.min(yMin, y);
				yMax = Math.max(yMax, y);
			}

			y += data[Math.min(j1, dataSize - 1)];
			y /= 2;

			yMin = Math.min(yMin, y);
			yMax = Math.max(yMax, y);

			int x = canvasBeginX + i;
			int y0 = ZMath.bound(canvasBeginY, ZMath.roundPositive(yt.transform(yMax)), canvasEndY);
			int y1 = ZMath.bound(canvasBeginY, ZMath.roundPositive(yt.transform(yMin)), canvasEndY);
			g.drawVerticalLine(x, y0, y1);
		}
	}
	private final Stroke stroke;
	private final Color color;
}
