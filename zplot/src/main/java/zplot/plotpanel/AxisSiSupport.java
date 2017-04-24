package zplot.plotpanel;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import zplot.utility.Interval;
import zplot.utility.SiUnits;
import zplot.utility.SwingUtils;

public class AxisSiSupport implements PlotAxisSupport {

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public NumberFormat getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(NumberFormat format) {
		labelFormat = format;
	}

	public int getIdealTickStepPx() {
		return idealTickStepPx;
	}

	public void setIdealTickStepPx(int stepPx) {
		idealTickStepPx = stepPx;
	}

	public int[] getTickStepCoefns() {
		return tickStepCoefns;
	}

	public void setTickStepCoefns(int[] coefns) {
		tickStepCoefns = coefns;
	}

	@Override
	public void init(Graphics2D g, int sizePx, Interval rangeHz) {
		if (rangeHz.size() != 0.0) {
			double d = Math.log10(Math.max(Math.abs(rangeHz.begin()), Math.abs(rangeHz.end())));
			logSiScale = 3 * ((int) d / 3);
			siScale = Math.pow(10.0, logSiScale);

			double sizeHz = rangeHz.size();
			double guessDataStepHz = (Math.min(idealTickStepPx, sizePx) * sizeHz) / sizePx;
			int N = (int) Math.log10(guessDataStepHz);

			for (int k = -1; k <= 1; ++k) {
				boolean stop = false;
				for (int i = 0; i != tickStepCoefns.length; ++i) {
					int exp = N + k;

					tickStepHz = tickStepCoefns[i] * Math.pow(10.0, exp);

					int dp = Math.max(0, logSiScale - exp);
					axisFormat.setMinimumFractionDigits(dp);
					axisFormat.setMaximumFractionDigits(dp);

					String minValue = axisFormat.format(rangeHz.begin() / siScale);
					Rectangle minLabelBounds = SwingUtils.getStringBounds(g, minValue);
					String maxValue = axisFormat.format(rangeHz.end() / siScale);
					Rectangle maxLabelBounds = SwingUtils.getStringBounds(g, maxValue);
					int labelWidth = Math.max(minLabelBounds.width, maxLabelBounds.width);

					int tickStepPx = (int) (tickStepHz * sizePx / rangeHz.size());
					if (labelWidth + 5 <= tickStepPx) {
						stop = true;
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} else {
			tickStepHz = 0.0;
			if (rangeHz.begin() != 0.0) {
				double d = Math.log10(Math.abs(rangeHz.begin()));
				logSiScale = 3 * ((int) d / 3);
				siScale = Math.pow(10.0, logSiScale);
			} else {
				logSiScale = 0;
				siScale = 1.0;
			}
			axisFormat.setMinimumFractionDigits(0);
			axisFormat.setMaximumFractionDigits(3);
		}
	}

	@Override
	public double tickStepHz() {
		return tickStepHz;
	}

	@Override
	public double firstTickMultipleHz() {
		return tickStepHz;
	}

	@Override
	public String formatTitle(String title) {
		StringBuilder sb = new StringBuilder(title);
		if (units != null) {
			sb.append(" (").append(SiUnits.getSiPrefix(logSiScale)).append(units).append(')');
		} else if (logSiScale != 0) {
			sb.append(" (\u00D710^").append(logSiScale).append(')');
		}
		return sb.toString();
	}

	@Override
	public String formatAxisLabel(double d) {
		return axisFormat.format(d / siScale);
	}

	@Override
	public String formatPlotLabel(double d) {
		StringBuilder sb = new StringBuilder();
		sb.append(labelFormat.format(d / siScale));
		if (units != null) {
			sb.append(' ').append(SiUnits.getSiPrefix(logSiScale)).append(units);
		} else if (logSiScale != 0) {
			sb.append('E').append(logSiScale);
		}
		return sb.toString();
	}

	@Override
	public String formatDelta(double d) {
		return formatPlotLabel(d);
	}

	@Override
	public String toString() {
		return "AxisSiSupport{tickStepHz=" + tickStepHz + " logSiScale=" + logSiScale
				+ " siScale=" + siScale + "}";
	}

	// Settable data
	private int idealTickStepPx = 30;
	private int[] tickStepCoefns = {1, 2, 5};
	private NumberFormat labelFormat = new DecimalFormat("0.#####");
	private String units = null;
	// Internals
	private final NumberFormat axisFormat = new DecimalFormat("0.00");
	private double tickStepHz = -1.0;
	private double siScale = -1.0;
	private int logSiScale = 0;
}
