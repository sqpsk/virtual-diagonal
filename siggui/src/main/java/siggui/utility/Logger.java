package siggui.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    public static void error(Class<?> cls, Object msg) {
        log(cls, "ERROR", msg);
    }

    public static void error(Class<?> cls, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log(cls, "ERROR", sw.toString());
    }

    public static void info(Class<?> cls, Object msg) {
        log(cls, "INFO", msg);
    }

    public static void debug(Class<?> cls, Object msg) {
        log(cls, "DEBUG", msg);
    }

    // Overlaods
    public static void error(Object obj, Object msg) {
        error(obj.getClass(), msg);
    }

    public static void error(Object obj, Throwable e) {
        error(obj.getClass(), e);
    }

    public static void info(Object obj, Object msg) {
        info(obj.getClass(), msg);
    }

    public static void debug(Object obj, Object msg) {
        debug(obj.getClass(), msg);
    }

    private static void log(Class<?> cls, String level, Object msg) {
        System.out.println(cls.getSimpleName() + " " + level + " " + msg);
    }

    private Logger() {
    }
}
