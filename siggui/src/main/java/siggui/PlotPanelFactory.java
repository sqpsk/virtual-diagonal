package siggui;

import java.awt.Color;
import zplot.plotpanel.PlotPanel;
import zplot.tools.CrosshairTool;
import zplot.tools.ZoomTool;

public class PlotPanelFactory {

	public static PlotPanelFactory instance() {
		return INSTANCE;
	}

	protected PlotPanelFactory() {
	}

	public PlotPanel make() {
		PlotPanel plotPanel = new PlotPanel();
		// For more control over the display use addMouseListener
		plotPanel.setPlotBackground(plotBackground);
		plotPanel.getAxis().setAxisColor(axisColor);
		plotPanel.getAxis().setGridLineColor(gridLineColor);
		ZoomTool.addToPlot(plotPanel);
		CrosshairTool.addToPlot(plotPanel);
		return plotPanel;
	}

	public void setPlotBackground(Color c) {
		plotBackground = c;
	}

	public void setAxisColor(Color c) {
		axisColor = c;
	}

	public void setGridLineColor(Color c) {
		gridLineColor = c;
	}

	private static final PlotPanelFactory INSTANCE = new PlotPanelFactory();
	private Color plotBackground = null;
	private Color axisColor = Color.BLACK;
	private Color gridLineColor = Color.GRAY.brighter();
}
