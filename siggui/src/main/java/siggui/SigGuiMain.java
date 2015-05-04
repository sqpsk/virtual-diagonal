package siggui;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import siggui.api.ISigGuiController;
import siggui.api.ISigGuiView;
import siggui.api.IViewControllerFactory;
import siggui.utility.Logger;

public class SigGuiMain {

    private static final SigGuiNative INSTANCE = new SigGuiNative();

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
	setLookAndFeel("Nimbus");

	String[] factoryClasses = {
	    "siggui.spectrum.SpectrumFactory",
	    "siggui.timeseries.TimeseriesFactory"
	};

	ISigGuiController[] controllers = new ISigGuiController[factoryClasses.length];
	ISigGuiView[] views = new ISigGuiView[factoryClasses.length];
	for (int i = 0; i != factoryClasses.length; ++i) {
	    Class c = Class.forName(factoryClasses[i]);
	    IViewControllerFactory factory = (IViewControllerFactory) c.newInstance();
	    controllers[i] = factory.getController();
	    views[i] = factory.getView();
	}

	final SigGuiController controller = new SigGuiController(controllers);
	final SigGuiView view = new SigGuiView(controller, views);
	controller.setView(view);
	controller.showView(0);

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
