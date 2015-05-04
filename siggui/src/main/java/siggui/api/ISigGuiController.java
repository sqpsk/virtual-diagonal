package siggui.api;

import java.io.File;
import siggui.properties.PropertySet;

public interface ISigGuiController {

    void calculate(boolean isAdjusting);

    // Should not trigger a recalculation
    void setFile(File file, PropertySet properties);

    void notifyViewShown();
    
    void notifyViewHidden();

    File getFile();
    
}
