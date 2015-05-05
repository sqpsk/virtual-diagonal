package siggui.spectrum;

import siggui.api.ISigGuiController;
import siggui.api.ISigGuiView;
import siggui.api.IViewControllerFactory;

public class SpectrumFactory implements IViewControllerFactory {

	public SpectrumFactory() {
		controller = new SpectrumController();
		view = new SpectrumView(controller);
		view.setEnabled(false);
		controller.addPropertyChangeListener(view);
	}

	@Override
	public ISigGuiController getController() {
		return controller;
	}

	@Override
	public ISigGuiView getView() {
		return view;
	}

	private final SpectrumController controller;
	private final SpectrumView view;
}
