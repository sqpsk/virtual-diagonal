package siggui;

import java.io.File;
import javax.swing.JOptionPane;
import siggui.utility.Logger;

class SigGuiNative {

    // Make sure fftw is on the linking path
    public static void initNative() {
	try {
	    String nativeLib = System.getProperty("siggui_native_lib");
	    if (nativeLib == null) {
		System.loadLibrary("siggui-native");
	    } else {
		File f = new File(nativeLib);
		Logger.info(SigGuiNative.class, "Using native lib " + f.getCanonicalPath());
		System.load(f.getCanonicalPath());
	    }
	    initialize();
	} catch (Throwable e) {
	    Logger.info(SigGuiNative.class, "Working dir=" + new File(".").getAbsolutePath());
	    Logger.info(SigGuiNative.class, "java.library.path=" + System.getProperty("java.library.path"));
	    Logger.error(SigGuiNative.class, e);
	    JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().toString(), JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	}
    }

    static {
	initNative();
    }

    /**
     * Loads ClassIDs and MethodIDs to global memory.
     */
    private static native void initialize();

    /**
     * Unloads ClassIDs from global memory.
     */
    private static native void destroy();

    SigGuiNative() {
    }
}
