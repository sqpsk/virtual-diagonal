package zplot.utility;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public abstract class SaveImageAction implements ActionListener {

	/**
	 * Simple factory method where the filename is known at construction time.
	 * 
	 * @param component The component (most likely a PlotPanel) to save an image of.
	 * @param filename The default file name to use.
	 * @return The SaveImageAction which can (for example) be added to a JPopupMenu.
	 */
	public static SaveImageAction make(final Component component, final String filename) {
		return new SaveImageAction(component, "PNG") {
			@Override
			protected String getFilename() {
				return filename;
			}
		};
	}

	public SaveImageAction(Component component, String formatName) {
		this.component = component;
		this.formatName = formatName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save image");
		fc.setFileHidingEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(getFilename()));
		if (fc.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
			doSave(fc.getSelectedFile());
		}
	}

	protected abstract String getFilename();

	private void doSave(File file) {
		BufferedImage im = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		component.paint(im.getGraphics());
		try {
			ImageIO.write(im, formatName, file);
		} catch (IOException e) {
			System.out.println("SaveImageAction: ERROR: Faild to save image: " + e);
		}
	}

	private final Component component;
	private final String formatName;
}
