package com.commerzinfo.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public final class DecimalFormatUtil {
    //Numberformat 2.091,56+ or 100,00-
    private static final DecimalFormat HTML_FORMAT = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
    private static final DecimalFormat CSV_FORMAT = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);

    static {
        HTML_FORMAT.setNegativePrefix("");
        HTML_FORMAT.setNegativeSuffix("-");
        HTML_FORMAT.setPositiveSuffix("+");

        CSV_FORMAT.setNegativePrefix("-");
        CSV_FORMAT.setPositivePrefix("+");
    }

    private DecimalFormatUtil() {
    }

    public static Number parse(String source, Mode mode) throws ParseException {
        if (Mode.HTML.equals(mode))
            return HTML_FORMAT.parse(source);
        else if (Mode.CSV.equals(mode))
            return CSV_FORMAT.parse(source);
        else
            return null;
    }

    public enum Mode {
        HTML,
        CSV
    }
}
