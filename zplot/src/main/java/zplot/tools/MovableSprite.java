package zplot.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import zplot.plotpanel.PlotPanel;
import zplot.utility.Interval2D;
import zplot.utility.PopupSupport;
import zplot.utility.PriorityMouseListener;
import zplot.utility.PriorityMouseMotionListener;
import zplot.utility.SwingUtils;
import zplot.plotpanel.Paintable;

public abstract class MovableSprite implements PriorityMouseListener, PriorityMouseMotionListener, PlotTool, Paintable {

	public static void setClearPopupMenu(MovableSprite ms) {
		JPopupMenu popup = new JPopupMenu();
		popup.add(makeClearMenuItem(ms));
		ms.setComponentPopupMenu(popup);
	}

	public static JMenuItem makeClearMenuItem(final MovableSprite ms) {
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				ms.remove(ms.plot);
				ms.plot.repaint();
			}
		};
		return SwingUtils.makeMenuItem("Clear", l);
	}

	public void setComponentPopupMenu(JPopupMenu popup) {
		this.popup = popup;
		this.popupSupport = makePopupSupport();
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isMoving() {
		return clickOffset != null;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	public void startMove(Point p) {
		if (clickOffset == null) {
			if (isInsideSprite(p)) {
				int dx = bounds.getLocation().x - p.x;
				int dy = bounds.getLocation().y - p.y;
				clickOffset = new Point(dx, dy);
				setSelected(true);
				plot.repaint();
			}
		}
	}

	public void continueMove(Point p) {
		startMove(p);
		if (isMoving()) {
			setLocation(p.x + clickOffset.x, p.y + clickOffset.y);
			plot.repaint();
		}
	}

	public void endMove(Point p) {
		if (isMoving()) {
			setLocation(p.x + clickOffset.x, p.y + clickOffset.y);
			//plot.repaint();
		}
		clickOffset = null;
	}

	@Override
	public void add(PlotPanel plot) {
		this.plot = plot;
		this.popupSupport = makePopupSupport();
		plot.addPaintable(this);
		plot.addMouseListener(this, true);
		plot.addMouseMotionListener(this);
	}

	@Override
	public void remove(PlotPanel plot) {
		plot.removeMouseMotionListener(this);
		plot.removeMouseListener(this);
		plot.removePaintable(this);
	}

	// MouseListener interface
	@Override
	public boolean mousePressed(MouseEvent me) {
		if (isPopupShowing()) {
			return true;
		}
		if (!me.isPopupTrigger()) {
			startMove(me.getPoint());
		}
		boolean keepSelection = (me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;
		if (!isMoving() && !keepSelection) {
			setSelected(false);
		}
		return claimMouseAction(me.getPoint());
	}

	@Override
	public boolean mouseReleased(MouseEvent me) {
		if (isPopupShowing()) {
			return true;
		}
		if (!me.isPopupTrigger()) {
			boolean ret = isMoving();
			endMove(me.getPoint());
			return ret;
		} else if (popupSupport != null && isInsideSprite(me.getPoint())) {
			setSelected(true);
			clickOffset = null;
			popupSupport.show(me.getX(), me.getY());
			return true;
		}
		return false;
	}

	// No mouseClicked with popup trigger
	@Override
	public boolean mouseClicked(MouseEvent me) {
		if (isPopupShowing()) {
			clickOffset = null;
			popupSupport.hide();
			return true;
		}
		return claimMouseAction(me.getPoint());
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
		if (isPopupShowing()) {
			return true;
		}
		if (!me.isPopupTrigger()) {
			continueMove(me.getPoint());
		}
		return isMoving();
	}

	@Override
	public boolean mouseMoved(MouseEvent me) {
		return false;
	}

	// PaintableComponent interface
	@Override
	public void paintComponent(Graphics2D g, Interval2D r) {
		bounds = paintComponent(g, r, x, y);
	}

	/**
	 *
	 * @param g
	 * @param r
	 * @param x The x position of the top left corner.
	 * @param y The y position of the top left corner.
	 * @return
	 */
	protected abstract Rectangle paintComponent(Graphics2D g, Interval2D r, int x, int y);

	private boolean isInsideSprite(Point p) {
		return bounds != null && bounds.contains(p);
	}

	private boolean isPopupShowing() {
		return popupSupport != null && popupSupport.isShowing();
	}

	private boolean claimMouseAction(Point p) {
		return isMoving() || isInsideSprite(p);
	}

	private PopupSupport makePopupSupport() {
		if (plot != null && popup != null) {
			return new PopupSupport(plot, popup);
		} else {
			return null;
		}
	}

	protected PlotPanel plot = null;
	protected JPopupMenu popup = null;
	private Point clickOffset = null;
	private boolean isSelected = false;
	// Requested location
	private int x = 0;
	private int y = 0;
	private Rectangle bounds = null;
	private PopupSupport popupSupport = null;
}
