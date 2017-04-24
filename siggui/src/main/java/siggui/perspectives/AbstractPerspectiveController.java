package siggui.perspectives;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import siggui.properties.PropertySet;
import siggui.utility.Logger;

public abstract class AbstractPerspectiveController<Parameters extends PerspectiveParameters, Result> implements PerspectiveController {

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

	public Parameters getParameters() {
		return parameters;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void calculate(boolean isAdjusting) {
		if (file == null) {
			return;
		}
		if (playThread != null) {
			playTask.notifyParametersChanged();
			return;
		}

		boolean taskRunning = task != null && !task.isDone();
		if (isAdjusting && taskRunning) {
			return;
		}

		recalculate = false;
		if (task != null) {
			task.removePropertyChangeListener(taskListener);
			task.cancel(true);
		}
//        changeSupport.firePropertyChange("calculate", null, parameters);
//        view.clear();
		task = newTask(parameters);
		task.addPropertyChangeListener(taskListener);
		task.execute();
	}

	@Override
	public void setFile(File file, PropertySet properties) {
		this.file = null;
		this.properties = properties;
		if (task != null) {
			task.removePropertyChangeListener(taskListener);
			task.cancel(true);
		}
		if (parameters != null) {
			parameters.removePropertyChangeListener(parametersListener);
		}
		if (file != null) {
			parameters = newParameters(properties);
			parameters.addPropertyChangeListener(parametersListener);
			changeSupport.firePropertyChange("parameters", null, parameters);
		}

		recalculate = true;
		this.file = file;
	}

	public void play() {
		if (playThread != null) {
			return;
		}
		changeSupport.firePropertyChange("play", null, null);
		playTask = new PlayTask<Parameters, Result>(this);
		playTask.notifyParametersChanged();
		playThread = new Thread(playTask);
		playThread.start();
	}

	public void stop() {
		if (playThread != null) {
			playThread.interrupt();
			playThread = null;
			playTask = null;
			changeSupport.firePropertyChange("stop", null, null);
		}
	}

	@Override
	public void notifyViewShown() {
		if (recalculate) {
			calculate(false);
		}
	}

	@Override
	public void notifyViewHidden() {
		stop();
	}

	protected abstract Parameters newParameters(PropertySet properties);

	protected abstract PerspectiveTask<Parameters, Result> newTask(Parameters parameters);

	protected void setResult(Parameters p, Result result) {
		if (p.isCalculationEquivalent(parameters)) {
			changeSupport.firePropertyChange("result", null, result);
		} else {
			Logger.error(this, "Ignoring " + p + " vs " + parameters);
		}
	}

	protected abstract void notifyParametersChanged(PropertyChangeEvent pce);

	private final PropertyChangeListener taskListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if ("result".equals(pce.getPropertyName())) {
				PerspectiveTask<Parameters, Result> task
						= (PerspectiveTask<Parameters, Result>) pce.getNewValue();
				setResult(task.getParameters(), task.getResult());
			}
		}
	};
	// Listen to parameters, recalculate if neccesary
	private final PropertyChangeListener parametersListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			notifyParametersChanged(pce);
		}
	};
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	protected Parameters parameters = null;
	protected PropertySet properties = null;
	private File file = null;
	private boolean recalculate = true;
	private PerspectiveTask<Parameters, Result> task = null;
	private Thread playThread = null;
	private PlayTask<Parameters, Result> playTask = null;
}
