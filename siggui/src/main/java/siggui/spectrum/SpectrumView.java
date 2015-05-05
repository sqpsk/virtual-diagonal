package siggui.spectrum;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import siggui.PlotPanelFactory;
import siggui.api.AbstractSigGuiView;
import siggui.api.DefaultSaveImageAction;
import siggui.api.PlayPanel;
import siggui.rangecontrols.BlockCoverRangeModel;
import siggui.utility.AdaptiveRangeSupport;
import siggui.utility.SwingFuns;
import zplot.data.ISeries;
import zplot.data.RegularSeries;
import zplot.data.SeriesCollection;
import zplot.data.SeriesCollectionBuilder;
import zplot.plotpanel.PlotAxis;
import zplot.plotpanel.PlotPanel;
import zplot.plotter.LinearLinePlotter;
import zplot.transformers.FillCanvasTransformer;
import zplot.transformers.FixedScaleTransformer;
import zplot.transformers.IDataTransformer;

class SpectrumView extends JPanel implements AbstractSigGuiView {

	SpectrumView(SpectrumController controller) {
		super(new BorderLayout());
		this.controller = controller;
		PlotAxis axis = plot.getAxis();
		axis.setXAxisTitle("Frequency");
		axis.setXAxisUnits("Hz");
		axis.setYAxisTitle("Power");
		axis.setYAxisUnits("dB");
		rangePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel p = new JPanel(null);
		p.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(rangePanel);
		p.add(controlPanel);

		add(plot, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);

		controlPanel.addPropertyChangeListener(controlListener);
		JPopupMenu popup = new JPopupMenu();
		popup.add(getXAxisPopupMenu());
		popup.add(new JSeparator());
		popup.add(zplot.utility.SwingFuns.makeMenuItem("Save image", new DefaultSaveImageAction(plot, controller, "spectrum")));
		plot.setPriorityComponentPopupMenu(popup);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		plot.setEnabled(enabled);
		SwingFuns.setEnabled(rangePanel, enabled);
		SwingFuns.setEnabled(controlPanel, enabled);
	}

	private void notifyNewParameters(SpectrumParameters p) {
		if (parameters != null) {
			parameters.removePropertyChangeListener(parametersListener);
		}
		parameters = p;
		if (p != null) {
			p.addPropertyChangeListener(parametersListener);
			BlockCoverRangeModel rangeModel
					= BlockCoverRangeModel.make(p.getBegin(), p.getEnd(),
							p.getFileSizeSamples(), p.getFftSize(), p.getFftStep());
			controlPanel.setFftSizeOptions(p.getMinFftSize(), p.getMaxFftSize());
			controlPanel.updateControls(p);
			rangePanel.removeModelListener(rangeListener);
			rangePanel.setModel(rangeModel);
			rangePanel.addModelListener(rangeListener);
			setEnabled(true);
		}
	}

	private void notifyNewResult(SpectrumResult result) {
		if (result == null) {
			return;
		}
		double xBegin;
		double xEnd;
		switch (xAxisUnits) {
			default:
				xBegin = parameters.getPlotMinFreq();
				xEnd = parameters.getPlotMaxFreq();
				break;
			case RELATIVE_REQUENCY:
				xBegin = parameters.getPlotMinRelativeFreq();
				xEnd = parameters.getPlotMaxRelativeFreq();
				break;
			case FOURIER_BINS:
				xBegin = parameters.getPlotMinBin();
				xEnd = parameters.getPlotMaxBin();
				break;
		}
		SeriesCollectionBuilder scb = new SeriesCollectionBuilder();
		ISeries series = RegularSeries.makeFromBeginAndEnd(result.getPower(), xBegin, xEnd);
		scb.add(null, series, new LinearLinePlotter());
		IDataTransformer t = new FillCanvasTransformer();
		SeriesCollection sc = scb.build();
		if (yAdaptive != null) {
			yAdaptive.newRange(sc.envelope().y());
			t = new FixedScaleTransformer(t, null, yAdaptive.getAxisRange());
		}
		plot.init(sc, t);
		plot.resetAndRepaint();
		this.result = result;
	}

	@Override
	public String getTitle() {
		return "Spectrum";
	}

	@Override
	public Component toComponent() {
		return this;
	}

	private JMenu getXAxisPopupMenu() {
		JMenu menu = new JMenu("x-Axis");
		ButtonGroup bg = new ButtonGroup();
		{
			JMenuItem item = new JRadioButtonMenuItem("Absolute frequency (Hz)");
			item.setSelected(true);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					setXAxisUnits(AxisUnits.ABSOLTE_FREQUENCY);
				}
			});
			bg.add(item);
			menu.add(item);
		}
		{
			JMenuItem item = new JRadioButtonMenuItem("Fourier bins");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					setXAxisUnits(AxisUnits.FOURIER_BINS);
				}
			});
			bg.add(item);
			menu.add(item);
		}
		{
			JRadioButtonMenuItem item = new JRadioButtonMenuItem("Relative frequency");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					setXAxisUnits(AxisUnits.RELATIVE_REQUENCY);
				}
			});
			bg.add(item);
			menu.add(item);
		}
		return menu;
	}

	private void setXAxisUnits(AxisUnits units) {
		xAxisUnits = units;
		PlotAxis axis = plot.getAxis();
		switch (xAxisUnits) {
			default:
				axis.setXAxisTitle("Frequency");
				axis.setXAxisUnits("Hz");
				break;
			case RELATIVE_REQUENCY:
				axis.setXAxisTitle("Relative frequency");
				axis.setXAxisUnits(null);
				break;
			case FOURIER_BINS:
				axis.setXAxisTitle("Fourier bins");
				axis.setXAxisUnits(null);
				break;
		}
		notifyNewResult(result);
	}
	// Listen to parameters, update controls
	private final PropertyChangeListener parametersListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			SpectrumParameters p = controller.getParameters();
			((BlockCoverRangeModel) rangePanel.getModel()).setBlockSizeAndStep(p.getFftSize(), p.getFftStep(),
					p.getBegin(), p.getEnd());
			controlPanel.updateControls(p);
			if ("range".equals(pce.getPropertyName())) {
				rangePanel.getModel().setRange(parameters.getBegin(), parameters.getEnd());
			}
		}
	};
	// Listen to controls, notify model
	private final PropertyChangeListener rangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if ("range".equals(pce.getPropertyName()) && !rangePanel.isAdjusting()) {
				controller.getParameters().setRange(
						rangePanel.getModel().getBegin(),
						rangePanel.getModel().getEnd(),
						rangePanel.isAdjusting());
			} else if ("play".equals(pce.getPropertyName())) {
				controller.play();
			} else if ("stop".equals(pce.getPropertyName())) {
				controller.stop();
			}
		}
	};
	private final PropertyChangeListener controlListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if ("fftSize".equals(pce.getPropertyName())) {
				controller.getParameters().setFftSize((Integer) pce.getNewValue());
			} else if ("fftCount".equals(pce.getPropertyName())) {
				controller.getParameters().setFftCount((Integer) pce.getNewValue());
			} else if ("fftWindow".equals(pce.getPropertyName())) {
				controller.getParameters().setWindow((Integer) pce.getNewValue());
			}
		}
	};

	// Model listener
	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		if ("result".equals(pce.getPropertyName())) {
			notifyNewResult((SpectrumResult) pce.getNewValue());
		} else if ("parameters".equals(pce.getPropertyName())) {
			notifyNewParameters((SpectrumParameters) pce.getNewValue());
		} else if ("play".equals(pce.getPropertyName())) {
			yAdaptive = new AdaptiveRangeSupport();
		} else if ("stop".equals(pce.getPropertyName())) {
			yAdaptive = null;
			rangePanel.stop();
		}
//        else if ("calculation".equals(pce.getPropertyName())) {
//            clear();
//        }
	}

	private enum AxisUnits {

		ABSOLTE_FREQUENCY, RELATIVE_REQUENCY, FOURIER_BINS
	}
	private final SpectrumController controller;
	private final PlotPanel plot = PlotPanelFactory.instance().make();
	private final PlayPanel rangePanel = new PlayPanel();
	private final ControlPanel controlPanel = new ControlPanel();
	private SpectrumParameters parameters = null;
	private SpectrumResult result = null;
	private AxisUnits xAxisUnits = AxisUnits.ABSOLTE_FREQUENCY;
	private AdaptiveRangeSupport yAdaptive = null;
}
