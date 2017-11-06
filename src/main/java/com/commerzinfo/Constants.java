package com.commerzinfo;

import com.google.common.collect.ObjectArrays;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class Constants {
    public static final String[] HTML_PATTERNS = new String[]{"*.html", "*.htm", "*.html.bz2", "*.htm.bz2"};
    public static final IOFileFilter HTML_FILE_FILTER = new WildcardFileFilter(HTML_PATTERNS, IOCase.INSENSITIVE);

    private static final String[] CSV_PATTERNS = new String[]{"*.csv", "*.csv.bz2"};
    static final IOFileFilter CSV_FILE_FILTER = new WildcardFileFilter(CSV_PATTERNS, IOCase.INSENSITIVE);

    private static final String[] ALLOWED_PATTERNS = ObjectArrays.concat(HTML_PATTERNS, CSV_PATTERNS, String.class);
    static final IOFileFilter ALLOWED_FILE_FILTER = new WildcardFileFilter(ALLOWED_PATTERNS, IOCase.INSENSITIVE);
}
