package zplot.plotpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import zplot.utility.Interval2D;
import zplot.utility.SwingFuns;
import zplot.utility.ZGraphics2D;
import zplot.utility.ZMath;

public class PlotAxis {

    // Line
    // Tick
    // gap
    // Label
    // gap
    // title
    // gap
    public void setDrawGridLines(boolean b) {
	drawGridLines = b;
    }

    public void setAxisColor(Color c) {
	axisColor = c;
    }

    public void setGridLineColor(Color c) {
	gridLineColor = c;
    }

    public void setGridLineStroke(Stroke s) {
	gridLineStroke = s;
    }

    public IPlotAxisSupport getXAxisSupport() {
	return xSupport;
    }

    public void setXAxisSupport(IPlotAxisSupport supp) {
	xSupport = supp;
    }

    public void setXAxisRoundTickOffset(boolean b) {
	xData.roundTickOffset = b;
    }

    public void setXAxisTitle(String title) {
	xData.title = title;
	clearAxisSize();
    }

    public void setXAxisUnits(String units) {
	xSupport.setUnits(units);
    }

    public IPlotAxisSupport getYAxisSupport() {
	return ySupport;
    }

    public void setYAxisSupport(IPlotAxisSupport supp) {
	ySupport = supp;
    }

    public void setYAxisRoundTickOffset(boolean b) {
	yData.roundTickOffset = b;
    }

    public void setYAxisTitle(String title) {
	yData.title = title;
	clearAxisSize();
    }

    public void setYAxisUnits(String units) {
	ySupport.setUnits(units);
    }

    void setAxisRange(Interval2D axisRange) {
	this.axisRangeHz = axisRange;
    }

    void clearAxisSize() {
	xAxisHeightPx = -1;
	yAxisWidthPx = -1;
    }

    int xAxisHeightPx(Graphics2D g) {
	if (xAxisHeightPx < 0) {
	    xAxisHeightPx = calculateXaxisHeight(g);
	    assert xAxisHeightPx > 0;
	}
	return xAxisHeightPx;
    }

    int yAxisWidthPx(Graphics2D g, int height) {
	if (yAxisWidthPx < 0) {
	    yAxisWidthPx = calculateYaxisWidth(g, height);
	    assert yAxisWidthPx > 0;
	}
	return yAxisWidthPx;
    }

    void paintComponent(ZGraphics2D g, Interval2D canvas) {
	paintComponent(g, (int) canvas.x().begin(), (int) canvas.x().end(), (int) canvas.y().begin(), (int) canvas.y().end());
    }

    void paintComponent(ZGraphics2D g, int xBegin, int xEnd, int yBegin, int yEnd) {
	assert xAxisHeightPx > 0;
	assert yAxisWidthPx > 0;

	final int width = xEnd - xBegin;
	final int height = yEnd - yBegin;

	// Draw from the bottom up - this way we do not have to pre calculate the height
	xSupport.init(g, width - yAxisWidthPx, axisRangeHz.x());
	g.setColor(axisColor);
	drawXaxis(g, xBegin, xEnd, yBegin, yEnd, xSupport);

	if (xData.title != null) {
	    String s = xSupport.formatTitle(xData.title);
	    Rectangle titleBounds = SwingFuns.getStringBounds(g, s);
	    // Drawing letters with a tail (y) can get cut off
	    g.drawString(s, (xBegin + xEnd - titleBounds.width) / 2, yEnd - gapPx);
	}

	ySupport.init(g, height - xAxisHeightPx, axisRangeHz.y());
	drawYaxis(g, xBegin, xEnd, yBegin, yEnd, ySupport);

	if (yData.title != null) {
	    String s = ySupport.formatTitle(yData.title);
	    Rectangle titleBounds = SwingFuns.getStringBounds(g, s);
	    int x = xBegin + gapPx + titleBounds.height - 3;
	    int y = (yEnd + titleBounds.width) / 2;
	    SwingFuns.drawVerticalString(g, s, x, y);
	}
    }

    private int calculateXaxisHeight(Graphics2D g) {
	int sum = tickSizePx;
	sum += gapPx;

	int textHeight = SwingFuns.getStringBounds(g, "()").height;
	sum += textHeight;
	sum += gapPx;

	if (xData.title != null) {
	    sum += textHeight;
	    sum += gapPx;
	}
	return sum;
    }

    private int calculateYaxisWidth(Graphics2D g, int height) {
	ySupport.init(g, height - xAxisHeightPx, axisRangeHz.y());

	int sumPx = tickSizePx;
	sumPx += gapPx;

	String minValue = ySupport.formatAxisLabel(axisRangeHz.y().begin());
	Rectangle minLabelBounds = SwingFuns.getStringBounds(g, minValue);
	String maxValue = ySupport.formatAxisLabel(axisRangeHz.y().end());
	Rectangle maxLabelBounds = SwingFuns.getStringBounds(g, maxValue);
	sumPx += Math.max(minLabelBounds.width, maxLabelBounds.width);
	sumPx += gapPx;

	if (yData.title != null) {
	    Rectangle titleBounds = SwingFuns.getStringBounds(g, "()");
	    sumPx += titleBounds.height;
	    sumPx += gapPx;
	}
	return sumPx;
    }

    private void drawXaxis(ZGraphics2D g, int xBegin, int xEnd, int yBegin, int yEnd, IPlotAxisSupport supp) {
	final Stroke axisStroke = g.getStroke();
	final double tickStepHz = supp.tickStepHz();
	final int yTickBegin = yEnd - xAxisHeightPx;
	final int yTickBack = yTickBegin + tickSizePx - 1;

	// Draw the axis
	g.drawLine(xBegin + yAxisWidthPx, yTickBegin, xEnd - 1, yTickBegin);

	double value;
	if (axisRangeHz.x().size() != 0.0 && xData.roundTickOffset) {
	    value = Math.ceil(axisRangeHz.x().begin() / supp.firstTickMultipleHz()) * supp.firstTickMultipleHz();
	} else {
	    value = axisRangeHz.x().begin();
	}

	final int axisWidthPx = xEnd - xBegin - yAxisWidthPx;
	final double majorTickStepPx;
	double x;
	if (axisRangeHz.x().size() != 0.0) {
	    majorTickStepPx = tickStepHz * axisWidthPx / axisRangeHz.x().size();
	    x = xBegin + yAxisWidthPx - 1 + ((value - axisRangeHz.x().begin()) * axisWidthPx / axisRangeHz.x().size());
	} else {
	    majorTickStepPx = xEnd - xBegin;
	    x = (xBegin + xEnd + this.yAxisWidthPx) / 2;
	}

	int labelEnd = -1;
	while (x < xEnd) {
	    int xInt = ZMath.roundPositive(x);

	    if (drawGridLines) {
		g.setColor(gridLineColor);
		g.setStroke(gridLineStroke);
		g.drawLine(xInt, yBegin, xInt, yTickBegin);
		g.setColor(axisColor);
		g.setStroke(axisStroke);
	    }

	    g.drawLine(xInt, yTickBegin, xInt, yTickBack);
	    labelEnd = drawXLabel(g, supp.formatAxisLabel(value), labelEnd, xInt, yTickBack + 1 + gapPx, 3);

	    value += tickStepHz;
	    x += majorTickStepPx;
	}
    }

    private void drawYaxis(ZGraphics2D g, int xBegin, int xEnd, int yBegin, int yEnd, IPlotAxisSupport supp) {
	final Stroke axisStroke = g.getStroke();
	final double tickStepHz = supp.tickStepHz();
	final int xTickBegin = xBegin + yAxisWidthPx - tickSizePx;
	final int xTickBack = xTickBegin + tickSizePx - 1;

	// Draw axis
	g.drawLine(xTickBack, yBegin, xTickBack, yEnd - this.xAxisHeightPx);

	double value;
	if (axisRangeHz.y().size() != 0.0 && yData.roundTickOffset) {
	    value = Math.ceil(axisRangeHz.y().begin() / supp.firstTickMultipleHz()) * supp.firstTickMultipleHz();
	} else {
	    value = axisRangeHz.y().begin();
	}
	final int axisWidth = yEnd - yBegin - xAxisHeightPx;
	final double majorTickStepPx;
	double y;
	if (axisRangeHz.y().size() != 0.0) {
	    majorTickStepPx = tickStepHz * axisWidth / axisRangeHz.y().size();
	    y = yEnd - xAxisHeightPx - ((value - axisRangeHz.y().begin()) * axisWidth / axisRangeHz.y().size());
	} else {
	    majorTickStepPx = yEnd - yBegin;
	    y = (yBegin + yEnd - this.xAxisHeightPx) / 2;
	}
	while (y >= yBegin) {
	    int yInt = ZMath.roundPositive(y);

	    if (drawGridLines) {
		g.setColor(gridLineColor);
		g.setStroke(gridLineStroke);
		g.drawLine(xTickBack + 1, (int) yInt, xEnd - 1, yInt);
		g.setColor(axisColor);
		g.setStroke(axisStroke);
	    }

	    // Tick mark
	    g.drawLine(xTickBegin, yInt, xTickBack, yInt);

	    // Label
	    drawYLabel(g, supp.formatAxisLabel(value), xTickBegin - gapPx, yInt);
	    value += tickStepHz;
	    y -= majorTickStepPx;
	}
    }

    private static int drawXLabel(Graphics2D g, String label, int prevEnd, int x, int y, int gap) {
	Rectangle labelBounds = SwingFuns.getStringBounds(g, label);
	// labelBounds is one past bounding rectangle so subtract 1
	if (prevEnd + gap < x - (labelBounds.width / 2)) {
	    g.drawString(label, x - labelBounds.width / 2, y + labelBounds.height - 1);
	    return x + (labelBounds.width / 2);
	} else {
	    return prevEnd;
	}
    }

    private static void drawYLabel(Graphics2D g, String label, int x, int y) {
	Rectangle labelBounds = SwingFuns.getStringBounds(g, label);
	g.drawString(label, x - labelBounds.width - 1, y + labelBounds.height / 2);
    }

    private static class AxisData {

	String title = null;
	boolean roundTickOffset = true;
    }
    private static final int tickSizePx = 6; // Must be > 0
    private static final int gapPx = 3;
    private final AxisData xData = new AxisData();
    private final AxisData yData = new AxisData();
    private Color axisColor = Color.BLACK;
    private Color gridLineColor = Color.GRAY.brighter();
    private Stroke gridLineStroke = new BasicStroke(1);
    private boolean drawGridLines = true;
    private IPlotAxisSupport xSupport = new AxisSiSupport();
    private IPlotAxisSupport ySupport = new AxisSiSupport();
    // The numbers on the axis. axisRangeHz must contain dataEnvelopeHz.
    private Interval2D axisRangeHz = new Interval2D(-1.0, 1.0, -1.0, 1.0);
    private int xAxisHeightPx = -1;
    private int yAxisWidthPx = -1;
}
