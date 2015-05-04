package zplot.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import zplot.data.IOrderedSeries;
import zplot.data.ISeries;
import zplot.data.SeriesCollection;
import zplot.plotpanel.PlotPanel;
import zplot.plotpanel.IPaintable;
import zplot.plotter.IInterpolatingPlotter;
import zplot.transformers.FixedScaleTransformer;
import zplot.transformers.IDataTransformer;
import zplot.transformers.SquareAxisTransformer;
import zplot.utility.Interval;
import zplot.utility.Interval2D;
import zplot.utility.PriorityMouseListener;
import zplot.utility.PriorityMouseMotionListener;
import zplot.utility.ZMath;

public class ZoomTool implements PriorityMouseListener, PriorityMouseMotionListener, IPlotTool, IPaintable, PropertyChangeListener {

    public boolean isZoomed() {
        return current != origional;
    }

    public void setZoomColor(Color c) {
        zoomColor = c;
    }

    @Override
    public void add(PlotPanel plot) {
        this.plot = plot;
        plot.addPaintable(this);
        plot.addPropertyChangeListener(this);
        plot.addMouseListener(this, false);
        plot.addMouseMotionListener(this);
        notifyNewData();
    }

    @Override
    public void remove(PlotPanel plot) {
        plot.removeMouseMotionListener(this);
        plot.removeMouseListener(this);
        // Only remove paintable if not zoomed in
        if (!isZoomed()) {
            plot.removePropertyChangeListener(this);
            plot.removePaintable(this);
            this.plot = null;
        }
    }

    // MouseListener interface
    @Override
    public boolean mouseClicked(MouseEvent me) {
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1) {
            dragBegin = me.getPoint();
            plot.repaint();
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent me) {
        Point p = me.getPoint();
        if (dragBegin != null) {
            int xBegin = Math.min(dragBegin.x, p.x);
            int xEnd = Math.max(dragBegin.x, p.x);
            int yBegin = Math.min(dragBegin.y, p.y);
            int yEnd = Math.max(dragBegin.y, p.y);
            int c = 2;
            if (Math.abs(xBegin - xEnd) > c && (impl instanceof XZoomer || Math.abs(yBegin - yEnd) > c)) {
                setPlotDisplayRegion(getZoomRegion());
            }
            dragBegin = null;
            dragEnd = null;
            plot.repaint();
        }
        dragBegin = null;
        dragEnd = null;
        return false;
    }

    @Override
    public boolean mouseEntered(MouseEvent me) {
        return false;
    }

    @Override
    public boolean mouseExited(MouseEvent me) {
        return false;
    }

    // MouseMotionListener interface
    @Override
    public boolean mouseDragged(MouseEvent me) {
        if (dragBegin != null) {
            dragEnd = me.getPoint();
            plot.repaint();
        }
        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent me) {
        return false;
    }

    // PaintableComponent interface
    @Override
    public void paintComponent(Graphics2D g, Interval2D r) {
        if (isZoomed() && !unzoomShowing) {
            addUnzoomButton(r);
        } else if (!isZoomed() && unzoomShowing) {
            removeUnzoomButton();
        }

        if (dragBegin == null || dragEnd == null) {
            return;
        }

        Interval2D zr = getZoomRegion();

        Stroke stroke = g.getStroke();
        g.setStroke(DASHED_STROKE);
        g.setColor(zoomColor);
        impl.paintComponent(g, zr);
        g.setStroke(stroke);
    }

    // PropertyChangeListener interface
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(PlotPanel.NEW_DATA)) {
            if (pce.getNewValue() != current) {
                notifyNewData();
            }
        }
    }

    protected void addUnzoomButton(Interval2D r) {
        unzoomShowing = true;
        origionalLayout = plot.getLayout();
        unzoom = makeUnzoomButton();
        GroupLayout layout = new GroupLayout(plot);
        plot.setLayout(layout);
        int xGap = ((int) r.x().begin()) + 1;
        int yGap = ((int) r.y().begin());
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGap(xGap, xGap, xGap)
                .addComponent(unzoom)));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGap(yGap, yGap, yGap)
                .addComponent(unzoom)));
    }

    protected void removeUnzoomButton() {
        plot.remove(unzoom);
        plot.setLayout(origionalLayout);
        unzoom = null;
        origionalLayout = null;
        unzoomShowing = false;
    }

    protected JButton makeUnzoomButton() {
        JButton button = new JButton("Unzoom");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                reset();
                plot.resetAndRepaint();
            }
        });
        return button;
    }

    // Half open interval
    private Interval2D getZoomRegion() {
        int minX = Math.min(dragBegin.x, dragEnd.x);
        int maxX = Math.max(dragBegin.x, dragEnd.x);
        int minY = Math.min(dragBegin.y, dragEnd.y);
        int maxY = Math.max(dragBegin.y, dragEnd.y);

        final Interval2D canvas = plot.getPlotCanvas();
        minX = (int) ZMath.bound(canvas.x().begin(), minX, canvas.x().end());
        maxX = (int) ZMath.bound(canvas.x().begin(), maxX, canvas.x().end());
        minY = (int) ZMath.bound(canvas.y().begin(), minY, canvas.y().end());
        maxY = (int) ZMath.bound(canvas.y().begin(), maxY, canvas.y().end());

        return impl.getZoomRegion(canvas, minX, maxX, minY, maxY);
    }

    private void notifyNewData() {
        origional = plot.getDataTransformer();
        current = origional;
        SeriesCollection sc = plot.getSeriesCollection();
        
        boolean supportsXzooming = true;
        for (int i = 0; i != sc.size(); ++i) {
            SeriesCollection.Entry e = sc.get(i);
            supportsXzooming &= e.plotter instanceof IInterpolatingPlotter
                             && e.series instanceof IOrderedSeries;
        }
        
        if (origional instanceof SquareAxisTransformer) {
            impl = new SquareZoomer();
        } else if (supportsXzooming) {
            impl = new XZoomer();
        } else {
            impl = new FreeZoomer();
        }
    }

    private void reset() {
        if (origional != null) {
            plot.setDataTransformer(origional);
            current = origional;
        }
    }

    private void setPlotDisplayRegion(Interval2D zr) {
        if (origional == null) {
            return;
        }
        Interval2D dataZoom = plot.canvasToData().transformFlipY(zr);
        IDataTransformer trans = impl.getNewDataTransformer(dataZoom);
        // Gaurd against NEW_DATA property change
        current = trans;
        plot.setDataTransformer(trans);
        plot.resetAndRepaint();
    }

    private static Interval calculateYRange(SeriesCollection sc, Interval2D region) {
        double yMin = java.lang.Double.MAX_VALUE;
        double yMax = -java.lang.Double.MAX_VALUE;
        for (int j = 0; j != sc.size(); ++j) {
            SeriesCollection.Entry e = sc.get(j);
            ISeries series = e.series;
            for (int i = 0; i != series.size(); ++i) {
                double x = series.x(i);
                double y = series.y(i);
                if (ZMath.closedContains(region, x, y)) {
                    yMin = Math.min(yMin, y);
                    yMax = Math.max(yMax, y);
                }
            }

            IInterpolatingPlotter ip = (IInterpolatingPlotter) e.plotter;
            IOrderedSeries os = (IOrderedSeries) series;
            Double beginInterp = ip.interpY(os, region.x().begin());
            if (beginInterp != null) {
                yMin = Math.min(yMin, beginInterp);
                yMax = Math.max(yMax, beginInterp);
            }
            Double endInterp = ip.interpY(os, region.x().begin());
            if (endInterp != null) {
                yMin = Math.min(yMin, endInterp);
                yMax = Math.max(yMax, endInterp);
            }
        }
        return new Interval(yMin, yMax);
    }

    private static interface Impl {

        Interval2D getZoomRegion(Interval2D canvas, int minX, int maxX, int minY, int maxY);

        IDataTransformer getNewDataTransformer(Interval2D dataZoom);
        
        void paintComponent(Graphics2D g2, Interval2D zr);
    }

    private class XZoomer implements Impl {

        @Override
        public Interval2D getZoomRegion(Interval2D canvas, int minX, int maxX, int minY, int maxY) {
            return new Interval2D(minX, maxX, canvas.y().begin(), canvas.y().end());
        }
        
        @Override
        public void paintComponent(Graphics2D g, Interval2D zr) {
            g.drawLine((int) zr.x().begin(), (int) zr.y().begin(), (int) zr.x().begin(), (int) zr.y().end());
            g.drawLine((int) zr.x().end(), (int) zr.y().begin(), (int) zr.x().end(), (int) zr.y().end());
        }

        @Override
        public IDataTransformer getNewDataTransformer(Interval2D dataZoom) {
            Interval zoomYRange = calculateYRange(plot.getSeriesCollection(), dataZoom);
            return new FixedScaleTransformer(origional, new Interval2D(dataZoom.x(), zoomYRange));
        }
    }
    
    private class SquareZoomer implements Impl {

        @Override
        public Interval2D getZoomRegion(Interval2D canvas, int minX, int maxX, int minY, int maxY) {
            int size = Math.max(maxX - minX, maxY - minY);
            int dx = dragBegin.x < dragEnd.x ? 0 : -1;
            int dy = dragBegin.y < dragEnd.y ? 0 : -1;
            return Interval2D.fromCornerAndSize(dragBegin.x + dx * size, dragBegin.y + dy * size, size, size);
        }
        
        @Override
        public void paintComponent(Graphics2D g, Interval2D zr) {
            g.drawRect((int) zr.x().begin(), (int) zr.y().begin(), (int) zr.width(), (int) zr.height());
        }

        @Override
        public IDataTransformer getNewDataTransformer(Interval2D dataZoom) {
            return new FixedScaleTransformer(origional, dataZoom);
        }
    }
    
    private class FreeZoomer implements Impl {

        @Override
        public Interval2D getZoomRegion(Interval2D canvas, int minX, int maxX, int minY, int maxY) {
            int sizeX = maxX - minX;
            int sizeY = maxY - minY;
            int dx = dragBegin.x < dragEnd.x ? 0 : -1;
            int dy = dragBegin.y < dragEnd.y ? 0 : -1;
            return Interval2D.fromCornerAndSize(dragBegin.x + dx * sizeX, dragBegin.y + dy * sizeY, sizeX, sizeY);
        }
        
        @Override
        public void paintComponent(Graphics2D g, Interval2D zr) {
            g.drawRect((int) zr.x().begin(), (int) zr.y().begin(), (int) zr.width(), (int) zr.height());
        }

        @Override
        public IDataTransformer getNewDataTransformer(Interval2D dataZoom) {
            return new FixedScaleTransformer(origional, dataZoom);
        }
    }
    
    private static final Stroke DASHED_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    private JButton unzoom = null;
    private LayoutManager origionalLayout = null;
    private boolean unzoomShowing = false;
    private Color zoomColor = Color.BLACK;
    private PlotPanel plot = null;
    private IDataTransformer origional = null;
    private IDataTransformer current = null;
    private Impl impl = null;
    private Point dragBegin = null;
    private Point dragEnd = null;
}