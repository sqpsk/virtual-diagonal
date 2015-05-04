package zplot.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import zplot.data.ISeries;
import zplot.data.SeriesCollection;
import zplot.plotpanel.PlotAxis;
import zplot.plotpanel.PlotPanel;
import zplot.plotpanel.IPaintable;
import zplot.utility.Interval2D;
import zplot.utility.Interval2DTransform;
import zplot.utility.IntervalTransform;
import zplot.utility.PriorityMouseListener;
import zplot.utility.SwingFuns;
import zplot.utility.ZMath;

public class CrosshairTool implements PriorityMouseListener, IPlotTool, PropertyChangeListener, IPaintable {

    public CrosshairTool() {
        maxSprites = 3;
    }

    public CrosshairTool(int maxSprites) {
        this.maxSprites = maxSprites;
    }

    public void setFont(Font f) {
        font = f;
    }

    public void setDataPoint(Point2D.Double data) {
        for (DataLabel s : sprites) {
            if (s.getDataPoint().equals(data)) {
                setDefaultPosition(s);
                return;
            }
        }

        if (sprites.size() == maxSprites) {
            // Will be removed by property change listener
            IPlotTool s = sprites.getFirst();
            s.remove(plot);
        }
        DataLabel s = makeNewSprite(data);
        setDefaultPosition(s);
        s.add(plot);
        sprites.add(s);
    }

    @Override
    public void add(PlotPanel plot) {
        this.plot = plot;
        plot.addMouseListener(this, false);
        plot.addPropertyChangeListener(this);
        plot.addPaintable(this);
    }

    @Override
    public void remove(PlotPanel plot) {
        reset();
        plot.removePaintable(this);
        plot.removePropertyChangeListener(this);
        plot.removeMouseListener(this);
        this.plot = null;
    }

    // MouseListener interface
    @Override
    public boolean mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 1 && me.getButton() == MouseEvent.BUTTON1) {
            onClick(me.getPoint());
        }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent me) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent me) {
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

    // PropertyChangeListener interface
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (PlotPanel.NEW_DATA.equals(pce.getPropertyName())) {
            reset();
        } else if (!isResetting && PlotPanel.PAINTABLE_REMOVED.equals(pce.getPropertyName())) {
            sprites.remove(pce.getNewValue());
        }
    }

    public void reset() {
        isResetting = true;
        for (IPlotTool s : sprites) {
            s.remove(plot);
        }
        sprites.clear();
        isResetting = false;
    }

    private void onClick(Point p) {
        Point2D.Double dataPoint = getBestDataPoint(p);
        if (dataPoint != null) {
            setDataPoint(dataPoint);
            plot.repaint();
        }
    }

    private Point2D.Double getBestDataPoint(Point clicked) {
        Interval2D plotCanvas = plot.getPlotCanvas();
        if (clicked.x < plotCanvas.x().begin() || clicked.y > plotCanvas.y().end()) {
            return null;
        }

        Interval2DTransform dataToCanvas = plot.dataToCanvas();
        SeriesCollection sc = plot.getSeriesCollection();

        BestPoint best = findClosestEuclidianPoint(sc, dataToCanvas.x(), dataToCanvas.y(), clicked);

        if (best.seriesIndex < 0 || best.dataIndex < 0) {
            // No data.
            return null;
        }

        double xData = sc.get(best.seriesIndex).series.x(best.dataIndex);
        double yData = sc.get(best.seriesIndex).series.y(best.dataIndex);
        return new Point2D.Double(xData, yData);
    }

    protected DataLabel makeNewSprite(Point2D.Double data) {
        PlotAxis axis = plot.getAxis();
        StringBuilder sb = new StringBuilder(64);
        sb.append('(').append(axis.getXAxisSupport().formatPlotLabel(data.x));
        sb.append(", ").append(axis.getYAxisSupport().formatPlotLabel(data.y)).append(')');
        String label = sb.toString();
        DataLabel dataLabel = new DataLabel(data, label, font);
        MovableSprite.setClearPopupMenu(dataLabel);
        return dataLabel;
    }

    protected void setDefaultPosition(DataLabel s) {
        Interval2DTransform dataToCanvas = plot.dataToCanvas();
        int x = ZMath.roundPositive(dataToCanvas.x().transform(s.getDataPoint().x));
        int y = ZMath.roundPositive(dataToCanvas.y().transform(s.getDataPoint().y));
        s.setCrossPosition(x, y);
        s.setLocation(x + 20, y - 20);
    }

    @Override
    public void paintComponent(Graphics2D g, Interval2D canvas) {
        DataLabel t0 = null;
        DataLabel t1 = null;
        for (IPlotTool s : sprites) {
            if (s instanceof DataLabel) {
                DataLabel dl = (DataLabel) s;
                if (dl.isSelected()) {
                    if (t0 == null) {
                        t0 = dl;
                    } else if (t1 == null) {
                        t1 = dl;
                    } else {
                        return;
                    }
                }
            }
        }

        if (t0 != null && t1 != null) {
            if (font != null) {
                g.setFont(font);
            }
            Point2D.Double p0 = t0.getDataPoint();
            Point2D.Double p1 = t1.getDataPoint();
            if (p1.x < p0.x) {
                Point2D.Double t = p0;
                p0 = p1;
                p1 = t;
            }
            PlotAxis axis = plot.getAxis();

            String dx = "\u0394x = " + axis.getXAxisSupport().formatDelta(p1.x - p0.x);
            Rectangle dxBounds = SwingFuns.getStringBounds(g, dx);

            String dy = "\u0394y = " + axis.getYAxisSupport().formatDelta(p1.y - p0.y);
            Rectangle dyBounds = SwingFuns.getStringBounds(g, dy);

            int pad = 3;
            int yOffset = 5;
            int width = Math.max(dxBounds.width, dyBounds.width) + 2 * pad;
            int height = dxBounds.height + dyBounds.height + 3 * pad;
            int x = ((int) canvas.x().mid()) - width / 2;
            int y = (int) (canvas.y().end() - height - yOffset);

            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
            g.setColor(Color.YELLOW);
            
            g.drawString(dx, x + pad, y + pad + dxBounds.height);
            g.drawString(dy, x + pad, y + 2 * pad + dxBounds.height + dyBounds.height);
        }
    }

    private static class BestPoint {

        int seriesIndex = -1;
        int dataIndex = -1;
        double error = Double.MAX_VALUE;
    }

    private static BestPoint findClosestEuclidianPoint(
            SeriesCollection sc,
            IntervalTransform xt,
            IntervalTransform yt,
            Point clicked) {
        BestPoint best = new BestPoint();
        for (int i = 0; i != sc.size(); ++i) {
            ISeries series = sc.get(i).series;
            int bestDataIndex = -1;
            double bestError = Double.MAX_VALUE;
            for (int j = 0; j != series.size(); ++j) {
                double x = xt.transform(series.x(j)) - clicked.x;
                double y = yt.transform(series.y(j)) - clicked.y;
                double error = x * x + y * y;
                if (error < bestError) {
                    bestError = error;
                    bestDataIndex = j;
                }
            }
            if (bestError < best.error) {
                best.error = bestError;
                best.seriesIndex = i;
                best.dataIndex = bestDataIndex;
            }
        }
        return best;
    }
    private final LinkedList<DataLabel> sprites = new LinkedList<DataLabel>();
    private final int maxSprites;
    private PlotPanel plot = null;
    private boolean isResetting = false;
    private Font font = new Font("Dialog", Font.BOLD, 12);
}
