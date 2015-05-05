package siggui.api;

public interface IViewControllerFactory {

	ISigGuiController getController();

	ISigGuiView getView();
}
