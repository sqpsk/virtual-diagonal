package siggui;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import siggui.utility.Logger;
import siggui.perspectives.PerspectiveView;
import siggui.perspectives.PerspectiveController;
import siggui.perspectives.PerspectiveFactory;

public class SigGuiMain {

	private static void setLookAndFeel(String laf) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (laf.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.error(SigGuiMain.class, e);
		} catch (InstantiationException e) {
			Logger.error(SigGuiMain.class, e);
		} catch (IllegalAccessException e) {
			Logger.error(SigGuiMain.class, e);
		} catch (UnsupportedLookAndFeelException e) {
			Logger.error(SigGuiMain.class, e);
		}
	}

	public static void main(String args[]) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		SigGuiNative.loadNativeLibrary();

		setLookAndFeel("Nimbus");

		String[] perspectiveFactoryClasses = {
			"siggui.perspectives.spectrum.SpectrumFactory",
			"siggui.perspectives.timeseries.TimeseriesFactory"
		};

		PerspectiveController[] controllers = new PerspectiveController[perspectiveFactoryClasses.length];
		PerspectiveView[] views = new PerspectiveView[perspectiveFactoryClasses.length];
		for (int i = 0; i != perspectiveFactoryClasses.length; ++i) {
			Class c = Class.forName(perspectiveFactoryClasses[i]);
			PerspectiveFactory factory = (PerspectiveFactory) c.newInstance();
			controllers[i] = factory.getController();
			views[i] = factory.getView();
		}

		final SigGuiController controller = new SigGuiController(controllers);
		final SigGuiView view = new SigGuiView(controller, views);
		controller.setView(view);
		controller.showPerspective(0);

		if (args.length == 1) {
			controller.setFile((new File(args[0])).getCanonicalFile());
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				view.getFrame().setVisible(true);
			}
		});
	}
}
