package zplot.plotpanel;

import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import zplot.utility.Interval;
import zplot.utility.SwingUtils;

public class AxisTimeSupport implements PlotAxisSupport {

	@Override
	public String getUnits() {
		return null;
	}

	@Override
	public void setUnits(String units) {
	}

	public SimpleDateFormat getPlotLabelFormat() {
		return plotLabelFormat;
	}

	public void setPlotLabelFormat(SimpleDateFormat format) {
		this.plotLabelFormat = format;
	}

	@Override
	public void init(Graphics2D g, int sizePx, Interval rangeMillis) {
		if (rangeMillis.size() != 0.0) {
			tickStepMillis = calculateTickStep(g, sizePx, rangeMillis.size());
			axisTickFormat = new SimpleDateFormat(getTickLabelFormat(tickStepMillis));
		} else {
			tickStepMillis = 0;
			axisTickFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
	}

	@Override
	public double tickStepHz() {
		return tickStepMillis;
	}

	@Override
	public double firstTickMultipleHz() {
		return tickStepMillis;
	}

	@Override
	public String formatTitle(String title) {
		return title;
	}

	@Override
	public String formatAxisLabel(double d) {
		return axisTickFormat.format(new Date((long) d));
	}

	@Override
	public String formatPlotLabel(double d) {
		return plotLabelFormat.format(new Date((long) d));
	}

	@Override
	public String formatDelta(double d) {
		long time = (long) d;
		if (time == 0) {
			return "0ms";
		}
		StringBuilder sb = new StringBuilder();
		time = appendPart(sb, "d", time, 24 * 60 * 60 * 1000);
		time = appendPart(sb, "h", time, 60 * 60 * 1000);
		time = appendPart(sb, "min", time, 60 * 1000);
		time = appendPart(sb, "s", time, 1000);
		appendPart(sb, "ms", time, 1);
		return sb.toString();
	}

	private static long appendPart(StringBuilder sb, String units, long time, long q) {
		if (time > q) {
			if (sb.length() != 0) {
				sb.append(' ');
			}
			sb.append(time / q).append(units);
			return time % q;
		}
		return time;
	}

	// Choose step from standard set such that pixelWidth of ticks is close to idealTickStepPx.
	private static int calculateTickStep(Graphics2D g, int sizePx, double sizeMillis) {
		assert (sizePx > 0) : "sizePx=" + sizePx;
		assert (sizeMillis > 0) : "sizeHz=" + sizeMillis;
		for (int tickStep : TICK_STEP_MILLIS) {
			int minTickStepPx = SwingUtils.getStringBounds(g, getTickLabelFormat(tickStep)).width;
			double minDataStepMillis = (Math.min(minTickStepPx, sizePx) * sizeMillis) / sizePx;
			if (tickStep >= minDataStepMillis) {
				return tickStep;
			}
		}
		return TICK_STEP_MILLIS[TICK_STEP_MILLIS.length - 1];
	}

	private static String getTickLabelFormat(int tickStepMillis) {
		if (tickStepMillis < 1000) {
			return "HH:mm:ss.SSS";
		} else if (tickStepMillis < 60 * 1000) {
			return "HH:mm:ss";
		} else if (tickStepMillis < 60 * 60 * 1000) {
			return "HH:mm";
		} else if (tickStepMillis < 24 * 60 * 60 * 1000) {
			return "MM-dd HH:mm";
		} else {
			return "MM-dd";
		}
	}

	private static final int[] TICK_STEP_MILLIS = {
		1, 2, 5, 10, 20, 50, 100, 200, 500,
		1000, 2000, 5000, 10000, 20000,
		60 * 1000, 2 * 60 * 1000, 5 * 60 * 1000, 10 * 60 * 1000, 20 * 60 * 1000,
		60 * 60 * 1000, 2 * 60 * 60 * 1000, 6 * 60 * 60 * 1000, 12 * 60 * 60 * 1000,
		24 * 60 * 1000
	};
	private int tickStepMillis = -1;
	private SimpleDateFormat axisTickFormat = null;
	private SimpleDateFormat plotLabelFormat = new SimpleDateFormat("HH:mm:ss.SSS");
}
