package siggui.perspectives;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class PerspectiveParameters implements Cloneable {

	public PerspectiveParameters(long fileSizeSamples) {
		this.fileSizeSamples = fileSizeSamples;
	}

	public boolean isCalculationEquivalent(PerspectiveParameters other) {
		return begin == other.begin && end == other.end;
	}

	@Override
	public abstract PerspectiveParameters clone();

	public long getFileSizeSamples() {
		return fileSizeSamples;
	}

	public boolean isAdjusting() {
		return isAdjusting;
	}

	public long getBegin() {
		return begin;
	}

	public long getEnd() {
		return end;
	}

	public long getSize() {
		return end - begin;
	}

	public abstract void setRange(long begin, long end, boolean isAdjusting);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String name, PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(name, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String name, PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(name, l);
	}

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	protected final long fileSizeSamples;
	protected boolean isAdjusting = false;
	protected long begin;
	protected long end;
}
