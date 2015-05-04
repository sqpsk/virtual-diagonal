package siggui.timeseries;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
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
import siggui.rangecontrols.BoundedRangeModel;
import siggui.utility.AdaptiveRangeSupport;
import siggui.utility.SwingFuns;
import zplot.data.ISeries;
import zplot.data.InterleavedSeries;
import zplot.data.RegularSeries;
import zplot.data.SeriesCollection;
import zplot.data.SeriesCollectionBuilder;
import zplot.plotpanel.PlotAxis;
import zplot.plotpanel.PlotPanel;
import zplot.plotter.DotPlotter;
import zplot.plotter.IPlotter;
import zplot.plotter.LinearLinePlotter;
import zplot.tools.Legend;
import zplot.transformers.FillCanvasTransformer;
import zplot.transformers.FixedScaleTransformer;
import zplot.transformers.IDataTransformer;
import zplot.transformers.SquareAxisTransformer;

class TimeseriesView extends JPanel implements AbstractSigGuiView {

    TimeseriesView(TimeseriesController controller) {
        super(new BorderLayout());
        this.controller = controller;
        PlotAxis axis = plot.getAxis();
        axis.setYAxisTitle("Power");
        axis.setYAxisUnits("dB");
        axis.setXAxisTitle("Samples");
        rangePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel(null);
        p.setBorder(BorderFactory.createTitledBorder(""));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(rangePanel);
        p.add(controlPanel);

        add(plot, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        setEnabled(false);
        controlPanel.addPropertyChangeListener(controlListener);

        JPopupMenu popup = new JPopupMenu();
        popup.add(getXAxisPopupMenu());
        popup.add(new JSeparator());
        popup.add(zplot.utility.SwingFuns.makeMenuItem("Save image", new DefaultSaveImageAction(plot, controller, "timeseries")));
        plot.setPriorityComponentPopupMenu(popup);
        new Legend().add(plot);
    }

    private void notifyNewParameters(TimeseriesParameters p) {
        if (parameters != null) {
            parameters.removePropertyChangeListener(parametersListener);
        }
        parameters = p;
        if (p != null) {
            p.addPropertyChangeListener(parametersListener);
            BoundedRangeModel rangeModel = BoundedRangeModel.makeChecked(
                    p.getFileSizeSamples(),
                    TimeseriesParameters.MIN_REGION_SIZE,
                    TimeseriesParameters.MAX_REGION_SIZE,
                    p.getBegin(),
                    p.getEnd());
            controlPanel.setComplexView(p.isComplex());
            controlPanel.updateControls(p);
            rangePanel.removeModelListener(rangeListener);
            rangePanel.setModel(rangeModel);
            rangePanel.addModelListener(rangeListener);
            setEnabled(true);
        }
    }

    private IPlotter makePlotter(Color color) {
        if (parameters.getPlotType() == TimeseriesParameters.PLOT_LINE) {
            return new LinearLinePlotter(color);
        } else {
            return new DotPlotter(color);
        }
    }

    public void notifyNewResult(TimeseriesResultCache result) {
        this.result = result;
        final int seriesMask = parameters.getSeriesType();

        setAxisTitles(seriesMask);

        SeriesCollectionBuilder scb = new SeriesCollectionBuilder();
        if (seriesMask == TimeseriesParameters.SERIES_IvQ) {
            ISeries series = InterleavedSeries.makeScatterSeries(result.getSeries(TimeseriesParameters.SERIES_IvQ));
            scb.add(null, series, makePlotter(Color.BLUE));
        } else {
            double xBegin;
            double xEnd;
            switch (xAxisUnits) {
                default:
                    xBegin = result.getBegin();
                    xEnd = result.getEnd();
                    break;
                case TIME:
                    xBegin = result.getBegin() / parameters.getSampleRateHz();
                    xEnd = result.getEnd() / parameters.getSampleRateHz();
                    break;
            }
            if (seriesMask == TimeseriesParameters.SERIES_I_AND_Q) {
                ISeries seriesI = RegularSeries.makeFromBeginAndEnd(result.getSeries(TimeseriesParameters.SERIES_I), xBegin, xEnd);
                ISeries seriesQ = RegularSeries.makeFromBeginAndEnd(result.getSeries(TimeseriesParameters.SERIES_Q), xBegin, xEnd);
                scb.add("I", seriesI, makePlotter(Color.BLUE));
                scb.add("Q", seriesQ, makePlotter(Color.RED));
            } else {
                ISeries series = RegularSeries.makeFromBeginAndEnd(result.getSeries(seriesMask), xBegin, xEnd);
                scb.add(null, series, makePlotter(seriesMask == TimeseriesParameters.SERIES_Q ? Color.RED : Color.BLUE));
            }
        }
        SeriesCollection sc = scb.build();

        IDataTransformer t;
        if (seriesMask == TimeseriesParameters.SERIES_IvQ) {
            t = new SquareAxisTransformer();
            if (xAdaptive != null) {
                xAdaptive.newRange(sc.envelope().x());
                yAdaptive.newRange(sc.envelope().y());
                t = new FixedScaleTransformer(t, xAdaptive.getAxisRange(), yAdaptive.getAxisRange());
            }
        } else {
            t = new FillCanvasTransformer();
            if (yAdaptive != null) {
                yAdaptive.newRange(sc.envelope().y());
                t = new FixedScaleTransformer(t, null, yAdaptive.getAxisRange());
            }
        }

        plot.init(sc, t);
        plot.resetAndRepaint();
    }

    private void setAxisTitles(int seriesMask) {
        String yAxisTitle;
        String yAxisUnits;
        switch (seriesMask) {
            case TimeseriesParameters.SERIES_POWER:
                yAxisTitle = "Power";
                yAxisUnits = "dB";
                break;
            case TimeseriesParameters.SERIES_PHASE:
                yAxisTitle = "Phase";
                yAxisUnits = "rad";
                break;
            case TimeseriesParameters.SERIES_IvQ:
                yAxisTitle = "Q (Imag)";
                yAxisUnits = null;
                break;
            default:
                yAxisTitle = "Magnitude";
                yAxisUnits = null;
                break;
        }

        String xAxisTitle;
        String xAxisUnitsString;
        if (seriesMask == TimeseriesParameters.SERIES_IvQ) {
            xAxisTitle = "I (Real)";
            xAxisUnitsString = null;
        } else {
            switch (xAxisUnits) {
                default:
                    xAxisTitle = "Samples";
                    xAxisUnitsString = null;
                    break;
                case TIME:
                    xAxisTitle = "Time";
                    xAxisUnitsString = "s";
                    break;
            }
        }
        PlotAxis axis = plot.getAxis();
        axis.setXAxisTitle(xAxisTitle);
        axis.setXAxisUnits(xAxisUnitsString);
        axis.setYAxisTitle(yAxisTitle);
        axis.setYAxisUnits(yAxisUnits);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        plot.setEnabled(enabled);
        SwingFuns.setEnabled(rangePanel, enabled);
        SwingFuns.setEnabled(controlPanel, enabled);
    }

    @Override
    public String getTitle() {
        return "Time series";
    }

    @Override
    public Component toComponent() {
        return this;
    }

    private JMenu getXAxisPopupMenu() {
        JMenu menu = new JMenu("x-Axis");
        ButtonGroup bg = new ButtonGroup();
        {
            JMenuItem item = new JRadioButtonMenuItem("Samples");
            item.setSelected(true);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setXAxisUnits(AxisUnits.SAMPLES);
                }
            });
            bg.add(item);
            menu.add(item);
        }
        {
            JMenuItem item = new JRadioButtonMenuItem("Time");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setXAxisUnits(AxisUnits.TIME);
                }
            });
            bg.add(item);
            menu.add(item);
        }
        return menu;
    }

    private void setXAxisUnits(AxisUnits units) {
        xAxisUnits = units;
        notifyNewResult(result);
    }
    // Listen to controls, notify model
    private final PropertyChangeListener rangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            if ("range".equals(pce.getPropertyName())) {
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
    private final PropertyChangeListener parametersListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            JPopupMenu menu = plot.getPriorityComponentPopupMenu();
            menu.getComponent(0).setVisible(parameters.getSeriesType() != TimeseriesParameters.SERIES_IvQ);
            menu.getComponent(1).setVisible(parameters.getSeriesType() != TimeseriesParameters.SERIES_IvQ);
            if ("range".equals(pce.getPropertyName())) {
                rangePanel.getModel().setRange(parameters.getBegin(), parameters.getEnd());
            }
        }
    };
    private final PropertyChangeListener controlListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            if ("series".equals(pce.getPropertyName())) {
                controller.getParameters().setSeriesType((Integer) pce.getNewValue());
            } else if ("plotType".equals(pce.getPropertyName())) {
                controller.getParameters().setPlotType((Integer) pce.getNewValue());
            }
        }
    };

    // Model listener
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if ("result".equals(pce.getPropertyName())) {
            notifyNewResult((TimeseriesResultCache) pce.getNewValue());
        } else if ("parameters".equals(pce.getPropertyName())) {
            notifyNewParameters((TimeseriesParameters) pce.getNewValue());
        } else if ("play".equals(pce.getPropertyName())) {
            yAdaptive = new AdaptiveRangeSupport();
            xAdaptive = new AdaptiveRangeSupport();
            plot.getAxis().setXAxisRoundTickOffset(false);
        } else if ("stop".equals(pce.getPropertyName())) {
            rangePanel.stop();
            yAdaptive = null;
            xAdaptive = null;
            IDataTransformer t;
            if (parameters.getSeriesType() == TimeseriesParameters.SERIES_IvQ) {
                t = new SquareAxisTransformer();
            } else {
                t = new FillCanvasTransformer();
            }
            plot.setDataTransformer(t);
            plot.getAxis().setXAxisRoundTickOffset(true);
        }
    }

    private enum AxisUnits {

        SAMPLES, TIME
    }
    private final TimeseriesController controller;
    private final PlotPanel plot = PlotPanelFactory.instance().make();
    private final PlayPanel rangePanel = new PlayPanel();
    private final TimeseriesControlPanel controlPanel = new TimeseriesControlPanel();
    private TimeseriesParameters parameters = null;
    private TimeseriesResultCache result = null;
    private AxisUnits xAxisUnits = AxisUnits.SAMPLES;
    private AdaptiveRangeSupport yAdaptive = null;
    private AdaptiveRangeSupport xAdaptive = null;
}
