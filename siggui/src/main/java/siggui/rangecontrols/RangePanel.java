package siggui.rangecontrols;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public class RangePanel extends JPanel {

    public RangePanel() {
        super(new BorderLayout());
        add(slider, BorderLayout.NORTH);
        add(textFields, BorderLayout.WEST);
    }

    public void addModelListener(PropertyChangeListener l) {
        getModel().addPropertyChangeListener(l);
    }

    public void removeModelListener(PropertyChangeListener l) {
        getModel().removePropertyChangeListener(l);
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
    private final RangeSlider slider = new RangeSlider();
    private final RangeTextFields textFields = new RangeTextFields();
}
