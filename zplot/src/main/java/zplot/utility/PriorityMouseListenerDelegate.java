package zplot.utility;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PriorityMouseListenerDelegate implements MouseListener {

	public void add(PriorityMouseListener l, boolean greedy) {
		ArrayList<Detail> delegates = new ArrayList<Detail>(this.delegates);
		delegates.add(0, new Detail(l, greedy));
		this.delegates = delegates;
	}

	public void remove(PriorityMouseListener l) {
		ArrayList<Detail> delegates = new ArrayList<Detail>(this.delegates);
		delegates.remove(new Detail(l, true));
		this.delegates = delegates;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		boolean caught = false;
		for (Detail d : delegates) {
			if (!caught || d.greedy) {
				caught |= d.l.mouseClicked(me);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent me) {
		boolean caught = false;
		for (Detail d : delegates) {
			if (!caught || d.greedy) {
				caught |= d.l.mousePressed(me);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		boolean caught = false;
		for (Detail d : delegates) {
			if (!caught || d.greedy) {
				caught |= d.l.mouseReleased(me);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		boolean caught = false;
		for (Detail d : delegates) {
			if (!caught || d.greedy) {
				caught |= d.l.mouseEntered(me);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent me) {
		boolean caught = false;
		for (Detail d : delegates) {
			if (!caught || d.greedy) {
				caught |= d.l.mouseExited(me);
			}
		}
	}

	private static class Detail {

		public Detail(PriorityMouseListener l, boolean greedy) {
			this.l = l;
			this.greedy = greedy;
		}

		@Override
		public int hashCode() {
			return l.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return equals((Detail) obj);
		}

		public boolean equals(Detail d) {
			return l.equals(d.l);
		}

		final PriorityMouseListener l;
		final boolean greedy;
	}

	private ArrayList<Detail> delegates = new ArrayList<Detail>();
}
