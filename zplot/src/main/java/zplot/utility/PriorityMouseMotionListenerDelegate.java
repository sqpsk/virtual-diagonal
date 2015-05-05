package zplot.utility;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PriorityMouseMotionListenerDelegate implements MouseMotionListener {

	public void add(PriorityMouseMotionListener l) {
		ArrayList<PriorityMouseMotionListener> delegates = new ArrayList<PriorityMouseMotionListener>(this.delegates);
		delegates.add(0, l);
		this.delegates = delegates;
	}

	public void remove(PriorityMouseMotionListener l) {
		ArrayList<PriorityMouseMotionListener> delegates = new ArrayList<PriorityMouseMotionListener>(this.delegates);
		delegates.remove(l);
		this.delegates = delegates;
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		for (PriorityMouseMotionListener l : delegates) {
			if (l.mouseDragged(me)) {
				break;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		for (PriorityMouseMotionListener l : delegates) {
			if (l.mouseMoved(me)) {
				break;
			}
		}
	}
	private ArrayList<PriorityMouseMotionListener> delegates = new ArrayList<PriorityMouseMotionListener>();
}
