package siggui.perspectives;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class PlayControl {

	public static JToggleButton makePlayButton() {
		JToggleButton tb = new JToggleButton(null, playIcon, false);
		tb.setSelectedIcon(stopIcon);
		tb.setRolloverEnabled(false);
		return tb;
	}

	private static ImageIcon createImageIcon(String path, String description, Class<?> cls) {
		java.net.URL url = cls.getResource(path);
		return new ImageIcon(url, description);
	}

	private static final Icon playIcon = createImageIcon("/toolbarButtonGraphics/media/Play16.gif", "", PlayControl.class);
	private static final Icon stopIcon = createImageIcon("/toolbarButtonGraphics/media/Stop16.gif", "", PlayControl.class);

	private PlayControl() {
	}
}
