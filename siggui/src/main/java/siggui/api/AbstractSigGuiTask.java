package siggui.api;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import siggui.utility.Logger;

public abstract class AbstractSigGuiTask<Parameters, Result> extends SwingWorker<Result, Result> {

	public AbstractSigGuiTask(Parameters parameters) {
		this.p = parameters;
	}

	public Parameters getParameters() {
		return p;
	}

	public Result getResult() {
		return r;
	}

	@Override
	protected void done() {
		if (!isCancelled()) {
			try {
				r = get();
				firePropertyChange("result", null, this);
			} catch (ExecutionException e) {
				Logger.error(this, e);
			} catch (InterruptedException e) {
				Logger.error(this, e);
			}
		}
	}

	@Override
	protected Result doInBackground() throws Exception {
		final long startNs = System.nanoTime();
		Result result = doInBackgroundImpl();
		long endNs = System.nanoTime();
		calcTimeNanos = endNs - startNs;
		if (calcTimeNanos >= PROC_TIME_LOG_THRESHOLD) {
			Logger.debug(this, "time " + calcTimeNanos / (1000L * 1000L) + "ms");
		}
		return result;
	}

	public long getCalcTimeNanos() {
		return calcTimeNanos;
	}

	protected abstract Result doInBackgroundImpl();

	private static final long PROC_TIME_LOG_THRESHOLD = 500L * 1000L * 1000L;
	protected final Parameters p;
	private Result r;
	private long calcTimeNanos = -1;
}
