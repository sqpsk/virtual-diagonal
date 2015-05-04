package zplot.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import zplot.data.SeriesCollection;
import zplot.plotter.DotPlotter;
import zplot.plotter.LinePlotter;
import zplot.utility.Interval2D;
import zplot.utility.SwingFuns;
import zplot.utility.ZMath;

public class Legend extends MovableSprite {

    @Override
    public Rectangle paintComponent(Graphics2D g, Interval2D r, int x, int y) {
        SeriesCollection sc = plot.getSeriesCollection();
        ArrayList<LableInfo> labels = makeLabels(g, sc);

        final int keyValueGap = 2;
        final int legendEntryGap = 3;
        final int lineWidth = 15;
        final int dotSize = 5;

        int width = (labels.size() - 1) * legendEntryGap;
        int height = 1;
        for (LableInfo li : labels) {
            height = Math.max(height, li.bounds.height);
            width += lineWidth;
            width += keyValueGap;
            width += li.bounds.width;
        }

        x = ZMath.bound((int) r.x().begin(), x, (int) r.x().end() - width);
        y = ZMath.bound((int) r.y().begin(), y, (int) r.y().end() - height);

        Rectangle bounds = new Rectangle(x, y, width, height);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        for (LableInfo li : labels) {
            SeriesCollection.Entry e = li.entry;
            if (e.plotter instanceof LinePlotter) {
                LinePlotter lp = (LinePlotter) e.plotter;
                g.setColor(lp.getColor());
                g.setStroke(lp.getStroke());
                SwingFuns.drawHorizontalLine(g, x, y + height / 2, lineWidth);
                x += lineWidth;
                x += keyValueGap;
            } else if (e.plotter instanceof DotPlotter) {
                DotPlotter dp = (DotPlotter) e.plotter;
                g.setColor(dp.getColor());
                g.fillRect(x + lineWidth / 2, y + (height - dotSize) / 2, dotSize, dotSize);
                x += lineWidth;
                x += keyValueGap;
            }

            g.setColor(Color.BLACK);
            g.drawString(li.label, x, y + li.bounds.height);
            x += li.bounds.width;
            x += legendEntryGap;
        }
        return bounds;
    }

    protected String makeLabel(SeriesCollection.Entry e) {
        Object key = e.key;
        if (key != null) {
            return key.toString();
        }
        return null;
    }

    private ArrayList<LableInfo> makeLabels(Graphics2D g, SeriesCollection sc) {
        ArrayList<LableInfo> labels = new ArrayList<LableInfo>(sc.size());
        for (int i = 0; i != sc.size(); ++i) {
            SeriesCollection.Entry e = sc.get(i);
            String label = makeLabel(e);
            if (label == null) {
                continue;
            }
            Rectangle bounds = SwingFuns.getStringBounds(g, label);
            labels.add(new LableInfo(e, label, bounds));
        }
        return labels;
    }

    private static class LableInfo {

        LableInfo(SeriesCollection.Entry entry, String label, Rectangle bounds) {
            this.entry = entry;
            this.label = label;
            this.bounds = bounds;
        }
        final SeriesCollection.Entry entry;
        final String label;
        final Rectangle bounds;
    }
}
