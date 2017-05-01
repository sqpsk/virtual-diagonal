package siggui.perspectives;

import java.awt.Component;
import zplot.utility.SaveImageAction;

public class DefaultSaveImageAction extends SaveImageAction {

	public DefaultSaveImageAction(Component component, PerspectiveController perspective, String plot) {
		super(component, "PNG");
		this.perspective = perspective;
		this.plot = plot;
	}

	@Override
	protected String getFilename() {
		return perspective.getFile().getName() + '_' + plot + ".png";
	}
	private final PerspectiveController perspective;
	private final String plot;
}
