package siggui.perspectives;

import java.io.File;
import siggui.properties.PropertySet;

public interface PerspectiveController {

	void calculate(boolean isAdjusting);

	// Should not trigger a recalculation
	void setFile(File file, PropertySet properties);

	void notifyViewShown();

	void notifyViewHidden();

	File getFile();

}
