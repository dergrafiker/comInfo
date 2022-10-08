package com.commerzinfo;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.time.format.DateTimeFormatter;

public class Constants {
    protected static final String[] HTML_PATTERNS = new String[]{"*.html", "*.htm", "*.html.bz2", "*.htm.bz2"};
    public static final IOFileFilter HTML_FILE_FILTER = new WildcardFileFilter(HTML_PATTERNS, IOCase.INSENSITIVE);
    public static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String[] CSV_PATTERNS = new String[]{"*.csv", "*.csv.bz2"};
    static final IOFileFilter CSV_FILE_FILTER = new WildcardFileFilter(CSV_PATTERNS, IOCase.INSENSITIVE);

    static final IOFileFilter ALLOWED_FILE_FILTER = new OrFileFilter(HTML_FILE_FILTER, CSV_FILE_FILTER);
}
