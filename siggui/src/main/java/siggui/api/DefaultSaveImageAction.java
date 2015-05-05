package siggui.api;

import java.awt.Component;
import zplot.utility.SaveImageAction;

public class DefaultSaveImageAction extends SaveImageAction {

	public DefaultSaveImageAction(Component component, ISigGuiController controller, String plot) {
		super(component, "PNG");
		this.controller = controller;
		this.plot = plot;
	}

	@Override
	protected String getFilename() {
		return controller.getFile().getName() + '_' + plot + ".png";
	}
	private final ISigGuiController controller;
	private final String plot;
}
