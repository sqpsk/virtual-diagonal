package siggui.api;

import javax.swing.SwingUtilities;
import siggui.utility.Logger;

class PlayTask<Parameters extends AbstractSigGuiParameters, Result> implements Runnable {

	PlayTask(AbstractSigGuiController<Parameters, Result> model) {
		this.model = model;
	}

	public void notifyParametersChanged() {
		@SuppressWarnings("unchecked")
		Parameters p = (Parameters) model.getParameters().clone();
		synchronized (paramLock) {
			parameters = p;
			paramLock.notify();
		}
	}

	@Override
	public void run() {
		try {
			runThrows();
		} catch (InterruptedException ignore) {
		} catch (Exception e) {
			Logger.error(this, e);
		} finally {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						model.stop();
					}
				});
			} catch (Exception ex) {
				Logger.error(this, ex);
			}
		}
	}

	private void runThrows() throws Exception {
		long begin = -1;
		long step = 1000;

		while (!Thread.interrupted()) {
			final Parameters p;
			synchronized (paramLock) {
				p = parameters;
			}

			if (begin != p.getBegin()) {
				begin = p.getBegin();
			} else {
				begin += step;
			}
			if (begin + p.getSize() >= p.getFileSizeSamples()) {
				break;
			}
			p.setRange(begin, begin + p.getSize(), false);

			final AbstractSigGuiTask<Parameters, Result> task = model.newTask(p);
			final Result result = task.doInBackground();
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Make sure the range always matches the plot.
					model.getParameters().setRange(p.getBegin(), p.getEnd(), false);
					model.setResult(p, result);
				}
			});

			// Play at some sort of watchable rate.
			step = Math.max(1, p.getSize() / 4);
			long sleepMillis = 50 - (task.getCalcTimeNanos() / (1000 * 1000));

			if (sleepMillis > 0) {
				synchronized (paramLock) {
					paramLock.wait(sleepMillis);
				}
			}
		}
	}
	private final Object paramLock = new Object();
	private final AbstractSigGuiController<Parameters, Result> model;
	private volatile Parameters parameters;
}
