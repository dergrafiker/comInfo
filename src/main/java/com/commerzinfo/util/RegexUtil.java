package com.commerzinfo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {
    private RegexUtil() {
    }

    private static final Matcher dateMatcher = Pattern.compile("\\d{2}.\\d{2}.\\d{4}").matcher("");
    private static final Matcher amountMatcher = Pattern.compile("(\\d+\\.)?\\d+,\\d+[-+]").matcher("");

    public static boolean matchesDate(String input) {
        dateMatcher.reset(input);
        return dateMatcher.matches();
    }

    public static boolean matchesAmount(String input) {
        amountMatcher.reset(input);
        return amountMatcher.matches();
    }
}
