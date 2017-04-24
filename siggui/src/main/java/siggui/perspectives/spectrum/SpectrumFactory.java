package siggui.perspectives.spectrum;

import siggui.perspectives.ViewControllerFactory;
import siggui.perspectives.PerspectiveView;
import siggui.perspectives.PerspectiveController;

public class SpectrumFactory implements ViewControllerFactory {

	public SpectrumFactory() {
		controller = new SpectrumController();
		view = new SpectrumView(controller);
		view.setEnabled(false);
		controller.addPropertyChangeListener(view);
	}

	@Override
	public PerspectiveController getController() {
		return controller;
	}

	@Override
	public PerspectiveView getView() {
		return view;
	}

	private final SpectrumController controller;
	private final SpectrumView view;
}
