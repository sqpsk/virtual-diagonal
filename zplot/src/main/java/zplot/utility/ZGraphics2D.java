package zplot.utility;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public final class ZGraphics2D extends Graphics2D {

    public ZGraphics2D(BufferedImage buf) {
        this.buf = buf;
        this.g = buf.createGraphics();
        this.pixels = ((DataBufferInt) buf.getRaster().getDataBuffer()).getData();
        this.drawBresenhamLines = false;
        this.rgb = g.getColor().getRGB();
    }

    public boolean isDrawBresenhamLines() {
        return drawBresenhamLines;
    }

    public void setDrawBresenhamLines(boolean b) {
        drawBresenhamLines = b;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        g.addRenderingHints(hints);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        g.clearRect(x, y, width, height);
    }

    @Override
    public void clip(Shape s) {
        g.clip(s);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        g.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        g.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public Graphics create() {
        return g.create();
    }

    @Override
    public void dispose() {
        g.dispose();
    }

    @Override
    public void draw(Shape s) {
        g.draw(s);
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        g.draw3DRect(x, y, width, height, raised);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        g.drawBytes(data, offset, length, x, y);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        g.drawChars(data, offset, length, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector gv, float x, float y) {
        g.drawGlyphVector(gv, x, y);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        g.drawImage(img, op, x, y);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return g.drawImage(img, xform, obs);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return g.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return g.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return g.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return g.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                observer);
    }

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        if (drawBresenhamLines) {
            drawBresenhamLine(x0, y0, x1, y1);
        } else {
            g.drawLine(x0, y0, x1, y1);
        }
    }

    public void drawHorizontalLine(int x0, int x1, int y) {
        int begin = y * buf.getWidth();
        int end = begin + x1 - x0 + 1;
        for (int i = begin; i != end; ++i) {
            setRGB(i);
        }
    }

    public void drawVerticalLine(int x, int y0, int y1) {
        int i = x + y0 * buf.getWidth();
        for (int y = y1 - y0 + 1; y != 0; --y) {
            setRGB(i);
            i += buf.getWidth();
        }
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        g.drawOval(x, y, width, height);
    }

    @Override
    public void drawPolygon(int[] points, int[] points2, int points3) {
        g.drawPolygon(points, points2, points3);
    }

    @Override
    public void drawPolygon(Polygon p) {
        g.drawPolygon(p);
    }

    @Override
    public void drawPolyline(int[] points, int[] points2, int points3) {
        g.drawPolyline(points, points2, points3);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        g.drawRenderableImage(img, xform);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        g.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        g.drawString(iterator, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        g.drawString(iterator, x, y);
    }

    @Override
    public void drawString(String s, float x, float y) {
        g.drawString(s, x, y);
    }

    @Override
    public void drawString(String str, int x, int y) {
        g.drawString(str, x, y);
    }

    @Override
    public void fill(Shape s) {
        g.fill(s);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int endAngle) {
        g.fillArc(x, y, width, height, startAngle, endAngle);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        g.fillOval(x, y, width, height);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(Polygon p) {
        g.fillPolygon(p);
    }

    // (x, y) is the top left corner
    @Override
    public void fillRect(int xBegin, int yBegin, int width, int height) {
        final int xEnd = xBegin + width;
        final int yEnd = yBegin + height;
        for (int y = yBegin; y != yEnd; ++y) {
            final int yy = y * buf.getWidth();
            for (int x = xBegin; x != xEnd; ++x) {
                setRGB(yy + x);
            }
        }
    }

    // TODO this does not respect the clip
    public void fillRect3x3(int xBegin, int yBegin) {
        int i = yBegin * buf.getWidth() + xBegin;
        setRGB(i);
        setRGB(i + 1);
        setRGB(i + 2);
        i += buf.getWidth();
        setRGB(i);
        setRGB(i + 1);
        setRGB(i + 2);
        i += buf.getWidth();
        setRGB(i);
        setRGB(i + 1);
        setRGB(i + 2);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public Color getBackground() {
        return g.getBackground();
    }

    @Override
    public Shape getClip() {
        return g.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
        return getClipBounds(new Rectangle());
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) {
        return g.getClipBounds(r);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Rectangle getClipRect() {
        return g.getClipRect();
    }

    @Override
    public Color getColor() {
        return g.getColor();
    }

    @Override
    public Composite getComposite() {
        return g.getComposite();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return g.getDeviceConfiguration();
    }

    @Override
    public Font getFont() {
        return g.getFont();
    }

    @Override
    public FontMetrics getFontMetrics() {
        return g.getFontMetrics();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return g.getFontMetrics(f);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return g.getFontRenderContext();
    }

    @Override
    public Paint getPaint() {
        return g.getPaint();
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return g.getRenderingHint(hintKey);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return g.getRenderingHints();
    }

    @Override
    public Stroke getStroke() {
        return g.getStroke();
    }

    @Override
    public AffineTransform getTransform() {
        return g.getTransform();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g.hit(rect, s, onStroke);
    }

    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        return g.hitClip(x, y, width, height);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        g.rotate(theta, x, y);
    }

    @Override
    public void rotate(double theta) {
        g.rotate(theta);
    }

    @Override
    public void scale(double sx, double sy) {
        g.scale(sx, sy);
    }

    @Override
    public void setBackground(Color color) {
        g.setBackground(color);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        g.setClip(x, y, width, height);
    }

    @Override
    public void setClip(Shape clip) {
        g.setClip(clip);
    }

    @Override
    public void setColor(Color c) {
        g.setColor(c);
        rgb = c.getRGB();
    }

    @Override
    public void setComposite(Composite comp) {
        g.setComposite(comp);
    }

    @Override
    public void setFont(Font font) {
        g.setFont(font);
    }

    @Override
    public void setPaint(Paint paint) {
        g.setPaint(paint);
    }

    @Override
    public void setPaintMode() {
        g.setPaintMode();
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        g.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        g.setRenderingHints(hints);
    }

    @Override
    public void setStroke(Stroke s) {
        g.setStroke(s);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        g.setTransform(Tx);
    }

    @Override
    public void setXORMode(Color c1) {
        g.setXORMode(c1);
    }

    @Override
    public void shear(double shx, double shy) {
        g.shear(shx, shy);
    }

    @Override
    public String toString() {
        return "ZGraphics2D[g = " + g.toString() + "]";
    }

    @Override
    public void transform(AffineTransform Tx) {
        g.transform(Tx);
    }

    @Override
    public void translate(double tx, double ty) {
        g.translate(tx, ty);
    }

    @Override
    public void translate(int x, int y) {
        g.translate(x, y);
    }

    public void setRGB(int i) {
        pixels[i] = rgb;
    }

    public void setRGB(int x, int y) {
        pixels[y * buf.getWidth() + x] = rgb;
    }

    private void drawBresenhamLine(int x0, int y0, int x1, int y1) {
        final int dx = Math.abs(x1 - x0);
        final int dy = Math.abs(y1 - y0);
        final int sx = x0 < x1 ? 1 : -1;
        final int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        do {
            setRGB(x0, y0);
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        } while (x0 != x1 || y0 != y1);
    }

    private final BufferedImage buf;
    private final Graphics2D g;
    private final int[] pixels;
    private boolean drawBresenhamLines;
    private int rgb;
}
