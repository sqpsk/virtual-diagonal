package siggui.spectrum;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class ControlPanel extends JPanel {

    private static void setEditable(JComboBox box) {
        Dimension d = box.getPreferredSize();
        box.setEditable(true);
        box.setPreferredSize(d);
    }

    ControlPanel() {
        super(null);
        setFftSizeOptions(SpectrumParameters.MIN_FFT_SIZE, SpectrumParameters.MAX_FFT_SIZE);
        setEditable(fftSizeBox);
        fftCountField.setPreferredSize(new JTextField("888888").getPreferredSize());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(new JLabel("FFT size"));
        row.add(fftSizeBox);
        row.add(new JLabel("FFT count"));
        row.add(fftCountField);
        row.add(new JLabel("Window"));
        row.add(windowBox);
        add(row);

        windowBox.addItemListener(windowListener);
    }

    public void setFftSizeOptions(int minFftSize, int maxFftSize) {
        fftSizeBox.removeItemListener(fftSizeListener);
        fftSizeBox.removeAllItems();
        for (int i = minFftSize; i <= maxFftSize; i *= 2) {
            fftSizeBox.addItem(i);
        }
        fftSizeBox.addItemListener(fftSizeListener);
    }

    public void updateControls(SpectrumParameters p) {
        removeControlListeners();
        fftSizeBox.setSelectedItem(p.getFftSize());
        fftCountField.setValue(p.getFftCount());
        addControlListeners();
    }

    private void removeControlListeners() {
        windowBox.removeItemListener(windowListener);
        fftSizeBox.removeItemListener(fftSizeListener);
        fftCountField.removePropertyChangeListener("value", fftCountListener);
    }

    private void addControlListeners() {
        windowBox.addItemListener(windowListener);
        fftSizeBox.addItemListener(fftSizeListener);
        fftCountField.addPropertyChangeListener("value", fftCountListener);
    }
    private final ItemListener fftSizeListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                firePropertyChange("fftSize", null, (Integer) fftSizeBox.getSelectedItem());
            }
        }
    };
    private final ItemListener windowListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                firePropertyChange("fftWindow", null, windowBox.getSelectedIndex());
            }
        }
    };
    private final PropertyChangeListener fftCountListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            firePropertyChange("fftCount", pce.getOldValue(), fftCountField.getValue());
        }
    };
    private final JComboBox<Integer> fftSizeBox = new JComboBox<Integer>();
    private final JFormattedTextField fftCountField = new JFormattedTextField();
    private final JComboBox<String> windowBox = new JComboBox<String>(new String[]{"Gaussian", "Triangular", "None"});
}
