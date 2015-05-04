package zplot.utility;

import java.text.DecimalFormat;

public class SiUnits {

    public static String getSiPrefix(int exponent) {
        switch (exponent) {
            case -24:
                return "y";
            case -21:
                return "z";
            case -18:
                return "a";
            case -15:
                return "f";
            case -12:
                return "p";
            case -9:
                return "n";
            case -6:
                return "u";
            case -3:
                return "m";
            case 0:
                return "";
            case 3:
                return "k";
            case 6:
                return "M";
            case 9:
                return "G";
            case 12:
                return "T";
            case 15:
                return "P";
            case 18:
                return "E";
            case 21:
                return "Z";
            case 24:
                return "Y";
            default:
                return "10^" + exponent;
        }
    }

    public static String toSiString(double value, String units, DecimalFormat df) {
        int exponent = 0;
        if (value >= 1) {
            while (value >= 1000.0) {
                value /= 1000;
                exponent += 3;
            }
        } else {
            while (value < 1.0) {
                value *= 1000;
                exponent -= 3;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(df.format(value));
        sb.append(getSiPrefix(exponent));
        sb.append(units);
        return sb.toString();
    }
}
