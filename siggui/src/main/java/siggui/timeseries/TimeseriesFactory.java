package siggui.timeseries;

import siggui.api.ISigGuiController;
import siggui.api.ISigGuiView;
import siggui.api.IViewControllerFactory;

public class TimeseriesFactory implements IViewControllerFactory {

	public TimeseriesFactory() {
		controller = new TimeseriesController();
		view = new TimeseriesView(controller);
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

	private final TimeseriesController controller;
	private final TimeseriesView view;
}
