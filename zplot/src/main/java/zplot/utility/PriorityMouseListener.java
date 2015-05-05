package zplot.utility;

import java.awt.event.MouseEvent;

public interface PriorityMouseListener {

	// mousePressed, mouseReleased, mouseClicked
	boolean mousePressed(MouseEvent me);

	boolean mouseReleased(MouseEvent me);

	boolean mouseClicked(MouseEvent me);

	boolean mouseEntered(MouseEvent me);

	boolean mouseExited(MouseEvent me);
}
