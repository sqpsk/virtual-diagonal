package siggui.perspectives.timeseries;

import siggui.perspectives.ViewControllerFactory;
import siggui.perspectives.PerspectiveView;
import siggui.perspectives.PerspectiveController;

public class TimeseriesFactory implements ViewControllerFactory {

	public TimeseriesFactory() {
		controller = new TimeseriesController();
		view = new TimeseriesView(controller);
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

	private final TimeseriesController controller;
	private final TimeseriesView view;
}
