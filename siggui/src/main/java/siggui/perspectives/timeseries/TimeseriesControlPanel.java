package siggui.perspectives.timeseries;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import siggui.utility.Logger;

class TimeseriesControlPanel extends JPanel {

	TimeseriesControlPanel() {
		super(null);
		ButtonGroup plotButtonGroup = new ButtonGroup();
		plotButtonGroup.add(realAndImagButton);
		plotButtonGroup.add(realButton);
		plotButtonGroup.add(imagButton);
		plotButtonGroup.add(iqScatterButton);
		plotButtonGroup.add(powerButton);
		plotButtonGroup.add(phaseButton);

		ButtonGroup typeButtonGroup = new ButtonGroup();
		typeButtonGroup.add(lineButton);
		typeButtonGroup.add(dotsButton);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		{
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.add(new JLabel("Series"));
			row.add(realAndImagButton);
			row.add(realButton);
			row.add(imagButton);
			row.add(iqScatterButton);
			row.add(powerButton);
			row.add(phaseButton);
			row.add(new JLabel("    Plot type"));
			row.add(lineButton);
			row.add(dotsButton);
			add(row);
		}

//        {
//            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            row.setAlignmentX(Component.LEFT_ALIGNMENT);
//            row.add(new JLabel("Type"));
//            row.add(lineButton);
//            row.add(dotsButton);
//            add(row, BorderLayout.EAST);
//        }
		powerButton.setSelected(true);
		lineButton.setSelected(true);

		realAndImagButton.addActionListener(seriesChangeListener);
		realButton.addActionListener(seriesChangeListener);
		imagButton.addActionListener(seriesChangeListener);
		iqScatterButton.addActionListener(seriesChangeListener);
		powerButton.addActionListener(seriesChangeListener);
		phaseButton.addActionListener(seriesChangeListener);
		dotsButton.addActionListener(plotTypeChangeListener);
		lineButton.addActionListener(plotTypeChangeListener);
	}

	public boolean isLinePlot() {
		return lineButton.isSelected();
	}

	public int getSeriesMask() {
		int mask = 0;
		if (realAndImagButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_I_AND_Q;
		} else if (realButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_I;
		} else if (imagButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_Q;
		} else if (iqScatterButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_IvQ;
		} else if (powerButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_POWER;
		} else if (phaseButton.isSelected()) {
			mask = TimeseriesParameters.SERIES_PHASE;
		}
		return mask;
	}

	public void setComplexView(boolean isComplex) {
		boolean complexOnlySeriesSelected = realAndImagButton.isSelected()
				|| imagButton.isSelected()
				|| iqScatterButton.isSelected()
				|| phaseButton.isSelected();

		realAndImagButton.setVisible(isComplex);
		imagButton.setVisible(isComplex);
		iqScatterButton.setVisible(isComplex);
		phaseButton.setVisible(isComplex);

		if (!isComplex && complexOnlySeriesSelected) {
			powerButton.setSelected(true);
		}
	}

	public void updateControls(TimeseriesParameters p) {
		switch (p.getSeriesType()) {
			case TimeseriesParameters.SERIES_POWER:
				powerButton.setSelected(true);
				break;
			case TimeseriesParameters.SERIES_PHASE:
				phaseButton.setSelected(true);
				break;
			case TimeseriesParameters.SERIES_I:
				realButton.setSelected(true);
				break;
			case TimeseriesParameters.SERIES_Q:
				imagButton.setSelected(true);
				break;
			case TimeseriesParameters.SERIES_IvQ:
				iqScatterButton.setSelected(true);
				break;
			case TimeseriesParameters.SERIES_I_AND_Q:
				realAndImagButton.setSelected(true);
				break;
			default:
				break;
		}

		if (p.getPlotType() == TimeseriesParameters.PLOT_LINE) {
			lineButton.setSelected(true);
		} else {
			dotsButton.setSelected(true);
		}
	}
	private final ActionListener seriesChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			firePropertyChange("series", -1, getSeriesMask());
		}
	};
	private final ActionListener plotTypeChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			firePropertyChange("plotType", null,
					isLinePlot() ? TimeseriesParameters.PLOT_LINE : TimeseriesParameters.PLOT_DOTS);
		}
	};
	private final JRadioButton realAndImagButton = new JRadioButton("I & Q");
	private final JRadioButton realButton = new JRadioButton("I");
	private final JRadioButton imagButton = new JRadioButton("Q");
	private final JRadioButton iqScatterButton = new JRadioButton("I vs Q");
	private final JRadioButton powerButton = new JRadioButton("Power");
	private final JRadioButton phaseButton = new JRadioButton("Phase");
	private final JRadioButton lineButton = new JRadioButton("Line");
	private final JRadioButton dotsButton = new JRadioButton("Dots");
}
