package zplot.utility;

public class ObjectUtils {

    public static boolean nullSafeCompare(Object a, Object b) {
	return a == b || (a != null && a.equals(b));
    }
}
