package siggui;

import java.io.File;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import siggui.api.ISigGuiController;
import siggui.properties.AbsoluteFilePath;
import siggui.properties.DurationProperty;
import siggui.properties.FileSizeProperty;
import siggui.properties.HeaderSizeProperty;
import siggui.properties.PropertiesTablePanel;
import siggui.properties.IProperty;
import siggui.properties.PropertySet;
import siggui.properties.SampleCountProperty;
import siggui.properties.SampleFormat;
import siggui.properties.SampleFormatProperty;
import siggui.properties.SampleRate;
import siggui.properties.WaveFileProperties;
import siggui.utility.CloseDialogAction;
import siggui.utility.JarFuns;
import siggui.utility.Logger;

class SigGuiController {

    SigGuiController(ISigGuiController[] controllers) {
	this.controllers = controllers;
	fc.setDialogTitle("Open file");
	fc.addChoosableFileFilter(new FileFilter() {
	    @Override
	    public boolean accept(File f) {
		return f.isDirectory() || f.getName().endsWith(".wav");
	    }

	    @Override
	    public String getDescription() {
		return "Wave files (*.wav)";
	    }
	});
	fc.setFileHidingEnabled(true);
    }

    public String getAppName() {
	return APP_NAME;
    }

    public void setView(SigGuiView view) {
	this.view = view;
	view.getFrame().setTitle(APP_NAME);
    }

    public void showView(int index) {
	ISigGuiController newController = controllers[index];
	if (newController != activeController) {
	    if (activeController != null) {
		activeController.notifyViewHidden();
	    }
	    activeController = newController;
	    activeController.notifyViewShown();
	    view.showView(index);
	}
    }

    public void setFile(File file) {
	if (file == null) {
	    return;
	}

	if (!file.exists()) {
	    JOptionPane.showMessageDialog(null, file, "File does not exist", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	final long fileSizeBytes = file.length();
	SampleFormat sampleFormat = null;
	long headerSizeBytes = -1;
	long sampleCount = -1;
	long sampleRateHz = -1;
	{
	    WaveFileProperties wav = readWavHeader(file.getAbsolutePath());
	    if (wav != null) {
		if (!wav.isValid() && wav.getSampleCount() >= MIN_SAMPLE_COUNT) {
		    sampleFormat = wav.getSampleFormat();
		    headerSizeBytes = wav.getHeaderSizeBytes();
		    sampleCount = wav.getSampleCount();
		    sampleRateHz = wav.getSampleRateHz();
		} else {
		    // TODO print some details
		    Logger.error(this, "Failed to read wav file");
		}
	    }
	}

	if (sampleFormat == null) {
	    openFilePanel.setFileSize(MIN_SAMPLE_COUNT, fileSizeBytes);
	    JDialog dlg = getModelDialog("Select sample format - " + file.getName(), openFilePanel);
	    openFilePanel.setDialog(dlg);
	    dlg.setVisible(true);
	    if (openFilePanel.okClicked()) {
		sampleFormat = openFilePanel.getSampleFormat();
		headerSizeBytes = openFilePanel.getHeaderSizeBytes();
		sampleCount = (fileSizeBytes - headerSizeBytes) / sampleFormat.bytesPerSample();
		sampleRateHz = openFilePanel.getSampleRateHz();
	    }
	}

	if (sampleFormat != null) {
	    assert headerSizeBytes >= 0;
	    assert sampleCount > 0;
	    assert sampleRateHz > 0;

	    view.setFile(file);
	    properties = new PropertySet();
	    properties.put(new AbsoluteFilePath(file.getAbsolutePath()));
	    properties.put(new FileSizeProperty(file.length()));
	    properties.put(new HeaderSizeProperty(headerSizeBytes));
	    properties.put(new SampleCountProperty(sampleCount));
	    properties.put(new SampleFormatProperty(sampleFormat));
	    properties.put(new SampleRate(sampleRateHz));
	    properties.put(new DurationProperty((double) sampleCount / (double) sampleRateHz));
	    setFile(file.getAbsolutePath(),
		    sampleFormat.isComplex,
		    sampleFormat.sampleType,
		    sampleFormat.bitsPerSample,
		    sampleFormat.isLittleEndian,
		    headerSizeBytes,
		    sampleRateHz);
	    for (ISigGuiController c : controllers) {
		c.setFile(file, properties);
	    }
	    activeController.calculate(false);
	}
    }

    void notifyExitActionPerformed() {
	System.exit(0);
    }

    void notifyOpenFileActionPerformed() {
	fc.showOpenDialog(view.getFrame());
	setFile(fc.getSelectedFile());
    }

    void notifyAboutActionPerformed() {
	StringBuilder sb = new StringBuilder(64);
	sb.append(APP_NAME);
	sb.append("\nVersion ");
	sb.append(JarFuns.getPomProperty("siggui", "siggui", "version", SigGuiController.class));
	JOptionPane.showMessageDialog(null, sb.toString(), "About", JOptionPane.INFORMATION_MESSAGE);
    }

    void notifyFilePropertiesActionPerformed() {
	Iterator<IProperty> i = properties.iterator();
	String[][] rows = new String[properties.size()][2];
	int index = 0;
	while (i.hasNext()) {
	    IProperty p = i.next();
	    rows[index][0] = p.displayName();
	    rows[index][1] = p.displayValue();
	    ++index;
	}
	JDialog dlg = getModelDialog("File properties", PropertiesTablePanel.makePropertiesPanel(rows));
	dlg.setVisible(true);
    }

    private JDialog getModelDialog(String title, JPanel p) {
	JDialog dlg = new JDialog(view.getFrame(), title, true);
	CloseDialogAction.addEscPressAction(dlg);
	dlg.add(p);
	dlg.setLocationByPlatform(true);
	dlg.setLocationRelativeTo(view.getFrame());
	dlg.setResizable(false);
	dlg.pack();
	return dlg;
    }

    private static native WaveFileProperties readWavHeader(String filename);

    private static native void setFile(
	    String filename,
	    boolean isComplex,
	    int sampleType,
	    int bitsPerSample,
	    boolean isLittleEndian,
	    long headerSizeBytes,
	    long sampleRateHz);

    private static final int MIN_SAMPLE_COUNT = 2;
    private static final String APP_NAME = "SigGUI";
    private final ISigGuiController[] controllers;
    private final JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
    private final OpenFilePanel openFilePanel = new OpenFilePanel();
    private ISigGuiController activeController;
    private SigGuiView view;
    private PropertySet properties;
}
