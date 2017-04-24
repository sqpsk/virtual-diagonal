package siggui.perspectives;

import java.awt.Component;

public interface PerspectiveView {

	String getTitle();

	Component toComponent();
}
