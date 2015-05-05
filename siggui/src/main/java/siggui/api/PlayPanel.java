package siggui.api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import siggui.rangecontrols.IRangeModel;
import siggui.rangecontrols.RangeSlider;
import siggui.rangecontrols.RangeTextFields;

public class PlayPanel extends JPanel {

	public PlayPanel() {
		super(new BorderLayout());
		add(slider, BorderLayout.NORTH);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(playButton);
		p.add(textFields);
		add(p, BorderLayout.WEST);
		playButton.addItemListener(itemListener);
	}

	public void stop() {
		playButton.setSelected(false);
	}

	public void addModelListener(PropertyChangeListener l) {
		getModel().addPropertyChangeListener(l);
		addPropertyChangeListener("play", l);
		addPropertyChangeListener("stop", l);
	}

	public void removeModelListener(PropertyChangeListener l) {
		getModel().removePropertyChangeListener(l);
		removePropertyChangeListener("play", l);
		removePropertyChangeListener("stop", l);
	}

	public boolean isAdjusting() {
		return slider.isAdjusting();
	}

	public IRangeModel getModel() {
		return slider.getModel();
	}

	public void setModel(IRangeModel model) {
		slider.setModel(model);
		textFields.setModel(model);
	}

	public void setRange(int begin, int end) {
		getModel().setRange(begin, end);
	}

	public void setBlockSize(int begin, int end) {
		getModel().setRange(begin, end);
	}

	private final ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent itemEvent) {
			int state = itemEvent.getStateChange();
			if (state == ItemEvent.SELECTED) {
				firePropertyChange("play", null, null);
			} else {
				firePropertyChange("stop", null, null);
			}
		}
	};

	private final RangeSlider slider = new RangeSlider();
	private final JToggleButton playButton = PlayControl.makePlayButton();
	private final RangeTextFields textFields = new RangeTextFields();
}
