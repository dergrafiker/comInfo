package com.commerzinfo.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public final class DecimalFormatUtil {
    private DecimalFormatUtil() {
    }

    //Numberformat 2.091,56+ or 100,00-
    private static final DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);

    static {
        df.setNegativePrefix("");
        df.setNegativeSuffix("-");
        df.setPositiveSuffix("+");
    }

    public static Number parse(String source) throws ParseException {
        return df.parse(source);
    }
}
