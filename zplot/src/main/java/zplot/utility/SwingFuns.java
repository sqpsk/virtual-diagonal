package zplot.utility;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class SwingFuns {

	public static JMenuItem makeMenuItem(String title, ActionListener l) {
		JMenuItem item = new JMenuItem(title);
		item.addActionListener(l);
		return item;
	}

	public static JRadioButtonMenuItem makeButtonMenuItem(String title, ActionListener l, ButtonGroup bg) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(title);
		item.addActionListener(l);
		bg.add(item);
		return item;
	}

	public static Rectangle getStringBounds(Graphics2D g, Font font, String s) {
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, s);
		return gv.getPixelBounds(null, 0.0f, 0.0f);
	}

	public static Rectangle getStringBounds(Graphics2D g, String s) {
		return getStringBounds(g, g.getFont(), s);
	}

	public static Rectangle getVerticalStringBounds(Graphics2D g, String s) {
		AffineTransform at = AffineTransform.getRotateInstance(-Math.PI / 2.0);
		Font derivedFont = g.getFont().deriveFont(at);
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = derivedFont.createGlyphVector(frc, s);
		return gv.getPixelBounds(null, 0.0f, 0.0f);
	}

	// The baseline of the leftmost character is at position (x, y).
	public static void drawVerticalString(Graphics2D g, String s, int x, int y) {
		final Font origionalFont = g.getFont();

		// Derive a new font using a rotatation transform
		AffineTransform at = AffineTransform.getRotateInstance(-Math.PI / 2.0);
		Font derivedFont = origionalFont.deriveFont(at);

		g.setFont(derivedFont);
		g.drawString(s, x, y);
		g.setFont(origionalFont);
	}

	public static void drawHorizontalLine(Graphics2D g, int x, int y, int width) {
		g.drawLine(x, y, x + width, y);
	}

	public static void drawVerticalLine(Graphics2D g, int x, int y, int height) {
		g.drawLine(x, y, x, y - height);
	}
}
