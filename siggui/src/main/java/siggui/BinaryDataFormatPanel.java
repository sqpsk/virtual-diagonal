package siggui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import siggui.properties.SampleFormat;

class BinaryDataFormatPanel extends JPanel {

    BinaryDataFormatPanel() {
	super(null);
	ButtonGroup realComplexButtonGroup = new ButtonGroup();
	realComplexButtonGroup.add(realButton);
	realComplexButtonGroup.add(imagButton);

	ButtonGroup typeButtonGroup = new ButtonGroup();
	typeButtonGroup.add(twosButton);
	typeButtonGroup.add(offsetButton);
	typeButtonGroup.add(floatButton);

	ButtonGroup sizeButtonGroup = new ButtonGroup();
	sizeButtonGroup.add(bits8Button);
	sizeButtonGroup.add(bits16Button);
	sizeButtonGroup.add(bits32Button);
	sizeButtonGroup.add(bits64Button);

	ButtonGroup endianButtonGroup = new ButtonGroup();
	endianButtonGroup.add(littleEndianButton);
	endianButtonGroup.add(bigEndianButton);

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	{
	    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    row.add(realButton);
	    row.add(imagButton);
	    add(row);
	}
	{
	    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    row.add(twosButton);
	    row.add(offsetButton);
	    row.add(floatButton);
	    add(row);
	}
	{
	    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    row.add(bits8Button);
	    row.add(bits16Button);
	    row.add(bits32Button);
	    row.add(bits64Button);
	    add(row);
	}
	{
	    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    row.add(littleEndianButton);
	    row.add(bigEndianButton);
	    add(row);
	}
	imagButton.setSelected(true);
	twosButton.setSelected(true);
	bits16Button.setSelected(true);
	littleEndianButton.setSelected(true);
	addControlListener(controlsListener);
    }

    SampleFormat getSampleFormat() {
	boolean isComplex = imagButton.isSelected();

	int type = SampleFormat.SAMPLE_TYPE_TWOS;
	if (offsetButton.isSelected()) {
	    type = SampleFormat.SAMPLE_TYPE_OFFSET;
	} else if (floatButton.isSelected()) {
	    type = SampleFormat.SAMPLE_TYPE_FLOAT;
	}

	int bits = 8;
	if (bits16Button.isSelected()) {
	    bits = 16;
	} else if (bits32Button.isSelected()) {
	    bits = 32;
	} else if (bits64Button.isSelected()) {
	    bits = 64;
	}

	boolean isLittleEndian = littleEndianButton.isSelected();

	return SampleFormat.make(isComplex, type, bits, isLittleEndian);
    }

    private void validateControls() {
	boolean isFloat = floatButton.isSelected();
	bits8Button.setEnabled(!isFloat);
	bits16Button.setEnabled(!isFloat);
	if (isFloat && (bits8Button.isSelected() || bits16Button.isSelected())) {
	    bits32Button.setSelected(true);
	}
	boolean is8bit = bits8Button.isSelected();
	littleEndianButton.setEnabled(!is8bit);
	bigEndianButton.setEnabled(!is8bit);
    }

    private void addControlListener(ActionListener l) {
	realButton.addActionListener(l);
	imagButton.addActionListener(l);
	twosButton.addActionListener(l);
	offsetButton.addActionListener(l);
	floatButton.addActionListener(l);
	bits8Button.addActionListener(l);
	bits16Button.addActionListener(l);
	bits32Button.addActionListener(l);
	bits64Button.addActionListener(l);
    }

    private void removeControlListener(ActionListener l) {
	realButton.removeActionListener(l);
	imagButton.removeActionListener(l);
	twosButton.removeActionListener(l);
	offsetButton.removeActionListener(l);
	floatButton.removeActionListener(l);
	bits8Button.removeActionListener(l);
	bits16Button.removeActionListener(l);
	bits32Button.removeActionListener(l);
	bits64Button.removeActionListener(l);
    }
    private final ActionListener controlsListener = new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent evt) {
	    removeControlListener(controlsListener);
	    validateControls();
	    addControlListener(controlsListener);
	    firePropertyChange("sampleFormat", null, getSampleFormat());
	}
    };
    private final JRadioButton realButton = new JRadioButton("Real");
    private final JRadioButton imagButton = new JRadioButton("Complex");
    private final JRadioButton twosButton = new JRadioButton("Integer (twos complement)");
    private final JRadioButton offsetButton = new JRadioButton("Integer (offset binary)");
    private final JRadioButton floatButton = new JRadioButton("Float");
    private final JRadioButton bits8Button = new JRadioButton("8-bit");
    private final JRadioButton bits16Button = new JRadioButton("16-bit");
    private final JRadioButton bits32Button = new JRadioButton("32-bit");
    private final JRadioButton bits64Button = new JRadioButton("64-bit");
    private final JRadioButton littleEndianButton = new JRadioButton("Little-endian");
    private final JRadioButton bigEndianButton = new JRadioButton("Big-endian");
}
