package siggui.perspectives;

public interface PerspectiveFactory {

	PerspectiveController getController();

	PerspectiveView getView();
}
