package zplot.utility;

import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class PopupMenuListener implements PriorityMouseListener {

    public PopupMenuListener(JComponent component, JPopupMenu popup) {
        this.component = component;
        this.popup = popup;
    }

    @Override
    public boolean mouseClicked(MouseEvent me) {
        boolean b = isShowing;
        isShowing = false;
        return b;
    }

    @Override
    public boolean mousePressed(MouseEvent me) {
        return isShowing;
    }

    @Override
    public boolean mouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            popup.show(component, me.getX(), me.getY());
            isShowing = true;
        }
        return isShowing;
    }

    @Override
    public boolean mouseEntered(MouseEvent me) {
        return isShowing;
    }

    @Override
    public boolean mouseExited(MouseEvent me) {
        return isShowing;
    }

    public JComponent getComponent() {
        return component;
    }

    public JPopupMenu getPopup() {
        return popup;
    }

    public boolean isIsShowing() {
        return isShowing;
    }
    
    private final JComponent component;
    private final JPopupMenu popup;
    private boolean isShowing = false;
}
