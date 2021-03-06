package com.commerzinfo.input.html;

import com.commerzinfo.Constants;
import com.commerzinfo.DataRow;
import com.commerzinfo.input.html.parse.BuchungszeilenParser;
import com.commerzinfo.util.CompressionUtil;
import com.google.common.collect.Lists;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HTMLParser {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HTMLParser.class);

    static {
        MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
    }

    private static List<String> getElementsFromFile(File file, String element) throws IOException {
        if (!Constants.HTML_FILE_FILTER.accept(file)) {
            throw new IllegalArgumentException(file.getAbsolutePath() +
                    " does not match " + StringUtils.join(Constants.HTML_PATTERNS, ','));
        }

        Source source = new Source(CompressionUtil.getCorrectInputStream(file));
        List<String> lines = Lists.newArrayList();
        for (Element currentElement : source.getAllElements(element)) {
            lines.add(currentElement.getContent().toString());
        }
        return lines;
    }

    public static List<DataRow> handleHTML(String elementToSearch, File file) throws IOException {
        List<DataRow> parsedRows;
        List<String> elementsFromFile = HTMLParser.getElementsFromFile(file, elementToSearch);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("%s has %d elements of type: %s",
                                      file, elementsFromFile.size(), elementToSearch));
        }
        parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("%s has %d parsed rows", file, parsedRows.size()));
        }
        return parsedRows;
    }
}
