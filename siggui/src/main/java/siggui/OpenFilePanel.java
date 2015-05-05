package siggui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import siggui.properties.SampleFormat;

class OpenFilePanel extends JPanel {

	private static JPanel makeRowPanel(int flowLayoutAlignment) {
		JPanel row = new JPanel(new FlowLayout(flowLayoutAlignment));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		return row;
	}

	OpenFilePanel() {
		super(null);
		dataFormatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerSizeField.setPreferredSize(new Dimension(60, headerSizeField.getPreferredSize().height));
		sampleRateField.setPreferredSize(new Dimension(60, sampleRateField.getPreferredSize().height));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(dataFormatPanel);
		{
			JPanel row = makeRowPanel(FlowLayout.LEFT);
			row.add(new JLabel("Header size (bytes)"));
			row.add(headerSizeField);
			add(row);
		}
		{
			JPanel row = makeRowPanel(FlowLayout.LEFT);
			row.add(new JLabel("Sample rate (Hz)"));
			row.add(sampleRateField);
			add(row);
		}
		{
			JPanel row = makeRowPanel(FlowLayout.CENTER);
			row.add(fileSizeLabel);
			add(row);
		}
		{
			JPanel row = makeRowPanel(FlowLayout.CENTER);
			row.add(sampleCountLabel);
			add(row);
		}

		JButton cancelButton = new JButton("Cancel");
		{
			JPanel row = makeRowPanel(FlowLayout.CENTER);
			row.add(okButton);
			row.add(cancelButton);
			add(row);
		}

		headerSizeField.setValue(0);
		sampleRateField.setValue(1.0);
		validateControls();

		PropertyChangeListener controlListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				validateControls();
			}
		};
		dataFormatPanel.addPropertyChangeListener("sampleFormat", controlListener);
		headerSizeField.addPropertyChangeListener("value", controlListener);
		sampleRateField.addPropertyChangeListener("value", controlListener);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				okClicked = true;
				dlg.setVisible(false);
				dlg.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dlg.setVisible(false);
				dlg.dispose();
			}
		});
	}

	public void setFileSize(int minSampleCount, long fileSizeBytes) {
		this.minSampleCount = minSampleCount;
		this.fileSizeBytes = fileSizeBytes;
		fileSizeLabel.setText("File size " + fileSizeBytes + "B");
	}

	public boolean okClicked() {
		return okClicked;
	}

	public SampleFormat getSampleFormat() {
		return dataFormatPanel.getSampleFormat();
	}

	public long getHeaderSizeBytes() {
		return headerSizeBytes;
	}

	public long getSampleRateHz() {
		return sampleRateHz;
	}

	public void setDialog(JDialog dlg) {
		this.dlg = dlg;
	}

	private void validateControls() {
		Object headerSize = headerSizeField.getValue();
		if (((Number) headerSize).intValue() < 0) {
			headerSizeField.setValue(0);
		}

		Object sampleRate = sampleRateField.getValue();
		if (((Number) sampleRate).doubleValue() <= 0.0) {
			sampleRateField.setValue(1.0);
		}

		SampleFormat sampleFormat = dataFormatPanel.getSampleFormat();
		headerSizeBytes = ((Number) headerSizeField.getValue()).longValue();
		sampleRateHz = ((Number) sampleRateField.getValue()).longValue();

		long sampleCount = (fileSizeBytes - headerSizeBytes) / sampleFormat.bytesPerSample();
		if (sampleCount >= minSampleCount) {
			sampleCountLabel.setText("Samples " + sampleCount);
		} else if (headerSizeBytes >= fileSizeBytes) {
			sampleCountLabel.setText("<html><font color=\"red\">Error: Header size &ge; file size</font></html>");
		} else {
			StringBuilder sb = new StringBuilder(128);
			sb.append("<html><font color=\"red\">");
			sb.append("Error: File contains ").append(sampleCount).append(sampleCount == 1 ? " sample" : " samples");
			sb.append("</font></html>");
			sampleCountLabel.setText(sb.toString());
		}
		okButton.setEnabled(sampleCount >= minSampleCount);
	}
	private final BinaryDataFormatPanel dataFormatPanel = new BinaryDataFormatPanel();
	private final JFormattedTextField headerSizeField = new JFormattedTextField();
	private final JFormattedTextField sampleRateField = new JFormattedTextField();
	private final JLabel fileSizeLabel = new JLabel();
	private final JLabel sampleCountLabel = new JLabel();
	private final JButton okButton = new JButton("OK");
	private int minSampleCount;
	private long fileSizeBytes;
	private JDialog dlg = null;
	private long headerSizeBytes = -1;
	private long sampleRateHz = -1;
	private boolean okClicked = false;
}
