package zplot.utility;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class PopupSupport {

	public PopupSupport(JComponent component, JPopupMenu popup) {
		this.component = component;
		this.popup = popup;
	}

	public boolean isShowing() {
		return isShowing;
	}

	public void show(int x, int y) {
		popup.show(component, x, y);
		isShowing = true;
	}

	public void hide() {
		isShowing = false;
	}

	private final JComponent component;
	private final JPopupMenu popup;
	private boolean isShowing = false;
}
