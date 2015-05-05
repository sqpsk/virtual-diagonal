package siggui.utility;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

public class CloseDialogAction extends AbstractAction {

	public static void addEscPressAction(JDialog dlg) {
		// Close the dialog when Esc is pressed
		String cancelName = "cancel";
		InputMap inputMap = dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
		ActionMap actionMap = dlg.getRootPane().getActionMap();
		actionMap.put(cancelName, new CloseDialogAction(dlg));
	}

	public static void addEscPressAction(JDialog dlg, AbstractAction action) {
		String cancelName = "cancel";
		InputMap inputMap = dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
		ActionMap actionMap = dlg.getRootPane().getActionMap();
		actionMap.put(cancelName, action);
	}

	public CloseDialogAction(final JDialog dlg) {
		this.dlg = dlg;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dlg.setVisible(false);
		dlg.dispose();
	}
	private final JDialog dlg;
}
