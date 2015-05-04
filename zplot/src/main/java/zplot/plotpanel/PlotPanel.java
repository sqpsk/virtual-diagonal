package zplot.plotpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import zplot.data.SeriesCollection;
import zplot.data.SeriesCollectionBuilder;
import zplot.transformers.FillCanvasTransformer;
import zplot.transformers.IDataTransformer;
import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;
import zplot.utility.IntervalTransform;
import zplot.utility.PopupMenuListener;
import zplot.utility.PriorityMouseListener;
import zplot.utility.PriorityMouseListenerDelegate;
import zplot.utility.PriorityMouseMotionListener;
import zplot.utility.PriorityMouseMotionListenerDelegate;
import zplot.utility.ZGraphics2D;

public class PlotPanel extends JComponent {

    public static final String NEW_DATA = "NEW_DATA";
    public static final String NEW_REGION_CALCULATOR = "NEW_REGION_CALCULATOR";
    public static final String NEW_PLOT = "NEW_PLOT";
    public static final String PAINTABLE_ADDED = "PAINTABLE_ADDED";
    public static final String PAINTABLE_REMOVED = "PAINTABLE_REMOVED";

    public Graphics2D getGraphics2D() {
	Graphics2D g2 = (Graphics2D) getGraphics();
	init(g2);
	return g2;
    }

    @Override
    public Dimension getPreferredSize() {
	if (super.isPreferredSizeSet()) {
	    return super.getPreferredSize();
	}
	return new Dimension(600, 300);
    }

    public void init(SeriesCollection seriesCollection, IDataTransformer transformer) {
	SeriesCollection oldSc = this.sc;
	IDataTransformer oldRc = this.transformer;
	this.sc = seriesCollection;
	this.transformer = transformer;
	reset();
	firePropertyChange(NEW_DATA, oldSc, seriesCollection);
	firePropertyChange(NEW_REGION_CALCULATOR, oldRc, transformer);
    }

    public SeriesCollection getSeriesCollection() {
	return sc;
    }

    public void setSeriesCollection(SeriesCollection seriesCollection) {
	init(seriesCollection, transformer);
    }

    public IDataTransformer getDataTransformer() {
	return transformer;
    }

    public void setDataTransformer(IDataTransformer transformer) {
	init(sc, transformer);
    }

    public Color getPlotBackground() {
	return plotBackground;
    }

    public void setPlotBackground(Color color) {
	plotBackground = color;
    }

    public PlotAxis getAxis() {
	return axis;
    }

    public void addPaintable(IPaintable paintable) {
	paintables.add(paintable);
	firePropertyChange(PAINTABLE_ADDED, null, paintable);
    }

    public void removePaintable(IPaintable paintable) {
	paintables.remove(paintable);
	firePropertyChange(PAINTABLE_REMOVED, null, paintable);
    }

    public void reset() {
	basePlot = null;
	basePlotCanvas = null;
    }

    public void resetAndRepaint() {
	reset();
	repaint();
    }

    public Interval2D getPlotCanvas() {
	return getPlotCanvas(getAxisCanvas(), (Graphics2D) getGraphics());
    }

    public Interval2D getUsedPlotCanvas() {
	return usedCanvas(sc.envelope(), getPlotCanvas());
    }

    public Interval2DTransform dataToCanvas() {
	return transformer.dataToCanvas(sc.envelope(), getPlotCanvas());
    }

    public Interval2DTransform canvasToData() {
	return dataToCanvas().inverseTransform();
    }

    public void setPriorityComponentPopupMenu(JPopupMenu popup) {
	if (popupMenuListener != null) {
	    removeMouseListener(popupMenuListener);
	}
	this.popupMenuListener = new PopupMenuListener(this, popup);
	addMouseListener(this.popupMenuListener, false);
    }

    public JPopupMenu getPriorityComponentPopupMenu() {
	if (this.popupMenuListener != null) {
	    return this.popupMenuListener.getPopup();
	}
	return null;
    }

    public void addMouseListener(PriorityMouseListener l, boolean greedy) {
	if (priorityMouseDelegate == null) {
	    priorityMouseDelegate = new PriorityMouseListenerDelegate();
	    addMouseListener(priorityMouseDelegate);
	}
	priorityMouseDelegate.add(l, greedy);
    }

    public void addMouseMotionListener(PriorityMouseMotionListener l) {
	if (priorityMouseMotionDelegate == null) {
	    priorityMouseMotionDelegate = new PriorityMouseMotionListenerDelegate();
	    addMouseMotionListener(priorityMouseMotionDelegate);
	}
	priorityMouseMotionDelegate.add(l);
    }

    public void removeMouseListener(PriorityMouseListener l) {
	if (priorityMouseDelegate != null) {
	    priorityMouseDelegate.remove(l);
	}
    }

    public void removeMouseMotionListener(PriorityMouseMotionListener l) {
	if (priorityMouseMotionDelegate != null) {
	    priorityMouseMotionDelegate.remove(l);
	}
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Rectangle vr = getVisibleRect();
	if (basePlot == null || basePlot.getWidth() != vr.width || basePlot.getHeight() != vr.height) {
	    paintBasePlotImage();
	    firePropertyChange(NEW_PLOT, null, basePlot);
	}

	// Draw the plot onto g.
	Graphics2D g2 = (Graphics2D) g;
	init(g2);
	g2.drawImage(basePlot, null, null);
	paintExtras(g2);
    }

    private void paintBasePlotImage() {
	final Rectangle vr = getVisibleRect();
	final Interval2D axisCanvas = getAxisCanvas();
	final BufferedImage image = imageFactory.getImage(vr.width, vr.height);
	final ZGraphics2D g2 = new ZGraphics2D(image);
	try {
	    g2.setFont(getFont());
	    init(g2);

	    // Set an approximate axis range
	    axis.setAxisRange(sc.envelope());
	    axis.clearAxisSize();

	    Interval2D plotCanvas = getPlotCanvas(axisCanvas, g2);
	    axis.setAxisRange(canvasToData().transformFlipY(plotCanvas));
	    axis.clearAxisSize();
	    plotCanvas = getPlotCanvas(axisCanvas, g2);

	    if (plotBackground != null) {
		g2.setColor(plotBackground);
		g2.fillRect((int) plotCanvas.x().begin(), (int) plotCanvas.y().begin(), (int) plotCanvas.width(), (int) plotCanvas.height());
	    }

	    if (plotCanvas != axisCanvas) {
		axis.paintComponent(g2, axisCanvas);
	    }
	    Interval2DTransform dataToCanvas = transformer.dataToCanvas(sc.envelope(), plotCanvas);
	    paintSeriesCollection(g2, dataToCanvas.x(), dataToCanvas.y(), plotCanvas);

	    this.basePlot = image;
	    this.basePlotCanvas = plotCanvas;
	} finally {
	    g2.dispose();
	}
    }

    protected void paintSeriesCollection(ZGraphics2D g2, IntervalTransform xt, IntervalTransform yt, Interval2D plotCanvas) {
	for (int i = 0; i != sc.size(); ++i) {
	    SeriesCollection.Entry e = sc.get(i);
	    if (!e.series.isEmpty()) {
		g2.setClip((int) plotCanvas.x().begin(),
			(int) plotCanvas.y().begin(),
			(int) (plotCanvas.width() + 1.0),
			(int) (plotCanvas.height() + 1.0));
		e.plotter.paintComponent(g2, e.series, xt, yt, plotCanvas);
	    }
	}
    }

    protected void paintExtras(Graphics2D g2) {
	for (int i = 0; i != paintables.size(); ++i) {
	    paintables.get(i).paintComponent(g2, basePlotCanvas);
	}
    }

    protected void init(Graphics2D g2) {
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    // The region in to which the series + grid lines are drawn
    private Interval2D getAxisCanvas() {
	final Rectangle vr = getVisibleRect();
	assert vr.x == 0;
	assert vr.y == 0;
	int xEnd = vr.width > 2 * RIGHT_BORDER_GAP ? vr.width - RIGHT_BORDER_GAP : vr.width;
	int yBegin = vr.height > 2 * TOP_BORDER_GAP ? TOP_BORDER_GAP : 0;
	return new Interval2D(0, xEnd, yBegin, vr.height);
    }

    private Interval2D getPlotCanvas(Interval2D axisCanvas, Graphics2D g2) {
	int bottomPad = axis.xAxisHeightPx(g2);
	int leftPad = axis.yAxisWidthPx(g2, (int) axisCanvas.height());
	if (axisCanvas.height() > 2 * bottomPad && axisCanvas.width() >= 2 * leftPad) {
	    return new Interval2D(axisCanvas.x().begin() + leftPad, axisCanvas.x().end(), axisCanvas.y().begin(), axisCanvas.y().end() - bottomPad);
	} else {
	    return axisCanvas;
	}
    }

    private Interval2D usedCanvas(Interval2D dataEnvelope, Interval2D plotCanvas) {
	Interval2DTransform dataToCanvas = transformer.dataToCanvas(dataEnvelope, plotCanvas);
	Interval2D t = dataToCanvas.transformFlipY(dataEnvelope);
	return Interval2D.intersection(plotCanvas, t);
    }

    private static class ImageFactory {

	BufferedImage getImage(int width, int height) {
	    if (buf == null || buf.getWidth() != width || buf.getHeight() != height) {
		buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    } else {
		int[] pixels = ((DataBufferInt) buf.getRaster().getDataBuffer()).getData();
		Arrays.fill(pixels, 0);
	    }
	    return buf;
	}
	private BufferedImage buf = null;
    }
    private static final int RIGHT_BORDER_GAP = 15;
    private static final int TOP_BORDER_GAP = 15;
    private final ArrayList<IPaintable> paintables = new ArrayList<IPaintable>();
    private final PlotAxis axis = new PlotAxis();
    // User settable data
    private SeriesCollection sc = SeriesCollectionBuilder.emptyCollection(-1.0, 1.0, -1.0, 1.0);
    private IDataTransformer transformer = new FillCanvasTransformer();
    private Color plotBackground = null;
    // Internals
    private final ImageFactory imageFactory = new ImageFactory();
    private BufferedImage basePlot = null;
    private Interval2D basePlotCanvas = null;
    private PriorityMouseListenerDelegate priorityMouseDelegate = null;
    private PriorityMouseMotionListenerDelegate priorityMouseMotionDelegate = null;
    private PopupMenuListener popupMenuListener = null;
}
