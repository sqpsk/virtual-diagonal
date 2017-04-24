package zplot.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import zplot.plotpanel.PlotPanel;
import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;
import zplot.utility.SwingUtils;
import zplot.utility.ZMath;

public class DataLabel extends MovableSprite implements PropertyChangeListener {

	public DataLabel(Point2D.Double dataPoint, String label, Font font) {
		this.dataPoint = dataPoint;
		this.label = label;
		this.font = font;
	}

	public DataLabel(Point2D.Double dataPoint, String label) {
		this.dataPoint = dataPoint;
		this.label = label;
		this.font = null;
	}

	public Point2D.Double getDataPoint() {
		return dataPoint;
	}

	@Override
	public void add(PlotPanel plot) {
		super.add(plot);
		if (font != null) {
			labelBounds = SwingUtils.getStringBounds(plot.getGraphics2D(), font, label);
		} else {
			labelBounds = SwingUtils.getStringBounds(plot.getGraphics2D(), label);
		}
		plot.addPropertyChangeListener(PlotPanel.NEW_PLOT_PROPERTY, this);
	}

	@Override
	public void remove(PlotPanel plot) {
		super.remove(plot);
		labelBounds = null;
		plot.removePropertyChangeListener(PlotPanel.NEW_PLOT_PROPERTY, this);
	}

	// PropertyChangeListener interface
	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		Interval2DTransform dataToCanvas = plot.dataToCanvas();
		int xCanvas = ZMath.roundPositive(dataToCanvas.x().transform(dataPoint.x));
		int yCanvas = ZMath.roundPositive(dataToCanvas.y().transform(dataPoint.y));
		setLocation(xCanvas + 20, yCanvas - 20);
		setCrossPosition(xCanvas, yCanvas);
		plot.repaint();
	}

	public void setCrossSize(int s) {
		s = 1 + 2 * (s / 2);
		hCrossSize = s / 2;
	}

	public void setCrossColor(Color c) {
		this.crossColor = c;
	}

	public void setValueColor(Color c) {
		this.valueColor = c;
	}

	public void setValueBackgroundColor(Color c) {
		this.valueBackgroundColor = c;
	}

	public void setCrossPosition(int x, int y) {
		this.crossX = x;
		this.crossY = y;
	}

	// x, y top left of label
	@Override
	public Rectangle paintComponent(Graphics2D g, Interval2D r, int x, int y) {
		if (!ZMath.closedContains(r, crossX, crossY)) {
			return null;
		}
		Color origionalColor = g.getColor();
		Font origionalFont = null;

		g.setColor(crossColor);
		drawCross(g, crossX, crossY, hCrossSize);

		x = ZMath.bound((int) r.x().begin(), x, (int) r.x().end() - labelBounds.width);
		y = ZMath.bound((int) r.y().begin(), y, (int) r.y().end() - labelBounds.height);

		int pad = 2;
		Rectangle bounds = new Rectangle(x, y, labelBounds.width + 2 + 2 * pad, labelBounds.height + 2 + 2 * pad);
		int centerX = bounds.x + (bounds.width / 2);
		int centerY = bounds.y + (bounds.height / 2);
		g.setColor(valueBackgroundColor);
		g.drawLine(crossX, crossY, centerX, centerY);

		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(valueColor);
		if (font != null) {
			origionalFont = g.getFont();
			g.setFont(font);
		}
		g.drawString(label, x + pad, y + pad + labelBounds.height);

		if (isSelected()) {
			g.setColor(Color.RED);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}

		g.setColor(origionalColor);
		if (origionalFont != null) {
			g.setFont(origionalFont);
		}
		return bounds;
	}

	private static void drawCross(Graphics2D g, int x, int y, int hSize) {
		g.drawLine(x - hSize, y, x + hSize, y);
		g.drawLine(x, y - hSize, x, y + hSize);
	}

	private final Point2D.Double dataPoint;
	private final String label;
	private final Font font;
	private Rectangle labelBounds = null;
	private Color crossColor = Color.RED;
	private Color valueColor = Color.YELLOW;
	private Color valueBackgroundColor = Color.GRAY;
	private int hCrossSize = 5;
	private int crossX = 0;
	private int crossY = 0;
}
