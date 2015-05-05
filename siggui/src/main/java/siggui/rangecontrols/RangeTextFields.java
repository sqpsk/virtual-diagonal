package siggui.rangecontrols;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RangeTextFields extends JPanel {

	public RangeTextFields() {
		super(new FlowLayout());
		add(new JLabel("Begin"));
		add(beginField);
		add(new JLabel("End"));
		add(endField);
		add(new JLabel("Size"));
		add(sizeField);
	}

	public IRangeModel getModel() {
		return model;
	}

	public void setModel(IRangeModel model) {
		if (this.model != null) {
			this.model.removePropertyChangeListener(modelChangeListener);
			removeControlListeners();
		}

		this.model = model;

		if (this.model != null) {
			updateControls();
			setFieldsPreferedSize();
			this.model.addPropertyChangeListener(modelChangeListener);
			// This avoids testing for a null model in propertyChange listeners
			addControlListeners();
		}
	}

	private void removeControlListeners() {
		beginField.removePropertyChangeListener("value", beginChangeListener);
		endField.removePropertyChangeListener("value", endChangeListener);
		sizeField.removePropertyChangeListener("value", sizeChangeListener);
	}

	private void addControlListeners() {
		beginField.addPropertyChangeListener("value", beginChangeListener);
		endField.addPropertyChangeListener("value", endChangeListener);
		sizeField.addPropertyChangeListener("value", sizeChangeListener);
	}

	private void setFieldsPreferedSize() {
		JFormattedTextField temp = new JFormattedTextField();
		temp.setValue(model.getFileSize());
		beginField.setPreferredSize(temp.getPreferredSize());
		endField.setPreferredSize(temp.getPreferredSize());
		sizeField.setPreferredSize(temp.getPreferredSize());
	}

	private void updateControls() {
		beginField.setValue(model.getBegin());
		endField.setValue(model.getEnd());
		sizeField.setValue(model.getSize());
		revalidate();
	}

	private final PropertyChangeListener modelChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			removeControlListeners();
			updateControls();
			addControlListeners();
		}
	};
	private final PropertyChangeListener beginChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			model.setBeginSameSizePriorityBegin(((Number) beginField.getValue()).longValue());
			beginField.setValue(model.getBegin());
		}
	};
	private final PropertyChangeListener endChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			model.setEndSameBeginPriorityBegin(((Number) endField.getValue()).longValue());
			endField.setValue(model.getEnd());
		}
	};
	private final PropertyChangeListener sizeChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			model.setSizeSameBeginPriorityBegin(((Number) sizeField.getValue()).longValue());
			sizeField.setValue(model.getSize());
		}
	};
	private final JFormattedTextField beginField = new JFormattedTextField();
	private final JFormattedTextField endField = new JFormattedTextField();
	private final JFormattedTextField sizeField = new JFormattedTextField();
	private IRangeModel model = null;
}
