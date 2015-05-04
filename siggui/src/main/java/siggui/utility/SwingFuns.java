package siggui.utility;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class SwingFuns {

    public static void setEnabled(JPanel panel, boolean enabled) {
        Component[] components = panel.getComponents();
        for (int i = 0; i != components.length; ++i) {
            Component c = components[i];
            if (c instanceof JPanel) {
                setEnabled((JPanel) c, enabled);
            } else {
                c.setEnabled(enabled);
            }
        }
        panel.setEnabled(enabled);
    }

    public static void hideTabs(JTabbedPane tabbedPane) {
        // Turn off show tabs on tabbed pane
        final boolean showTabsHeader = false;
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                if (showTabsHeader) {
                    return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
                } else {
                    return 0;
                }
            }
        });
    }

    private SwingFuns() {
    }
}
