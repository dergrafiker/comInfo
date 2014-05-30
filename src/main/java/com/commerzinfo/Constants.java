package com.commerzinfo;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class Constants {
    public static final String[] HTML_PATTERNS = new String[]{"*.html", "*.htm", "*.html.bz2", "*.htm.bz2"};
    public static final IOFileFilter HTML_FILE_FILTER = new WildcardFileFilter(HTML_PATTERNS,
            IOCase.INSENSITIVE);
}
