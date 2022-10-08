package com.commerzinfo.input.html;

import com.commerzinfo.Constants;
import com.commerzinfo.DataRow;
import com.commerzinfo.input.html.parse.BuchungszeilenParser;
import com.commerzinfo.util.CompressionUtil;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import org.apache.commons.codec.binary.StringUtils;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HTMLParser {

    static {
        MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
    }

    private static List<String> getElementsFromFile(File file, String element) throws IOException {
        if (!Constants.HTML_FILE_FILTER.accept(file)) {
            throw new IllegalArgumentException(file.getAbsolutePath() +
                    " does not match " + String.join(",", Constants.HTML_PATTERNS));
        }

        Source source = new Source(CompressionUtil.getCorrectInputStream(file));
        List<String> lines = new ArrayList<>();
        for (Element currentElement : source.getAllElements(element)) {
            lines.add(currentElement.getContent().toString());
        }
        return lines;
    }

    public static List<DataRow> handleHTML(String elementToSearch, File file) throws IOException {
        List<DataRow> parsedRows;
        List<String> elementsFromFile = HTMLParser.getElementsFromFile(file, elementToSearch);
        Logger.info(String.format("%s has %d elements of type: %s",
                file, elementsFromFile.size(), elementToSearch));
        parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
        Logger.info(String.format("%s has %d parsed rows", file, parsedRows.size()));
        return parsedRows;
    }
}
