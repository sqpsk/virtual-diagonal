package siggui.perspectives;

public interface ViewControllerFactory {

	PerspectiveController getController();

	PerspectiveView getView();
}
