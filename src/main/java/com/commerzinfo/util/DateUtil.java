package com.commerzinfo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {
    private DateUtil() {
    }

    private static final DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMAN);

    public static String format(Date date) {
        return dateInstance.format(date);
    }

    public static Date parse(String source) throws ParseException {
        return dateInstance.parse(source);
    }
}
