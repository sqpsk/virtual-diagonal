package siggui.perspectives.spectrum;

import siggui.perspectives.PerspectiveView;
import siggui.perspectives.PerspectiveController;
import siggui.perspectives.PerspectiveFactory;

public class SpectrumFactory implements PerspectiveFactory {

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
