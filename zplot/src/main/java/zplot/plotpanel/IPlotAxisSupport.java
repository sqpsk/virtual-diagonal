package zplot.plotpanel;

import java.awt.Graphics2D;
import zplot.utility.Interval;

public interface IPlotAxisSupport {

    String getUnits();

    void setUnits(String units);

    String formatAxisLabel(double d);

    String formatPlotLabel(double d);

    String formatDelta(double d);

    String formatTitle(String title);

    void init(Graphics2D g, int sizePx, Interval rangeHz);

    double tickStepHz();

    double firstTickMultipleHz();

}
