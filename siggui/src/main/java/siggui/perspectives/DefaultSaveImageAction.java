package siggui.perspectives;

import java.awt.Component;
import zplot.utility.SaveImageAction;

public class DefaultSaveImageAction extends SaveImageAction {

	public DefaultSaveImageAction(Component component, PerspectiveController controller, String plot) {
		super(component, "PNG");
		this.controller = controller;
		this.plot = plot;
	}

	@Override
	protected String getFilename() {
		return controller.getFile().getName() + '_' + plot + ".png";
	}
	private final PerspectiveController controller;
	private final String plot;
}
