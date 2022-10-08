package com.commerzinfo.input.html;

import com.commerzinfo.Constants;
import com.commerzinfo.DataRow;
import com.commerzinfo.input.html.parse.BuchungszeilenParser;
import com.commerzinfo.util.CompressionUtil;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HTMLParser {

    private static List<String> getElementsFromFile(File file, String element) throws IOException {
        if (!Constants.HTML_FILE_FILTER.accept(file)) {
            throw new IllegalArgumentException(file.getAbsolutePath() +
                    " does not match " + String.join(",", Constants.HTML_PATTERNS));
        }

        InputStream correctInputStream = CompressionUtil.getCorrectInputStream(file);
        String htmlText = IOUtils.toString(correctInputStream, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(htmlText);

        Elements elements = document.select(element);
        return elements.eachText();
    }

    public static List<DataRow> getAllElementTextsFromFile(String elementToSearch, File file) throws IOException {
        List<String> elementsFromFile = HTMLParser.getElementsFromFile(file, elementToSearch);
        Logger.info(String.format("%s has %d elements of type: %s", file, elementsFromFile.size(), elementToSearch));
        List<DataRow> parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
        Logger.info(String.format("%s has %d parsed rows", file, parsedRows.size()));
        return parsedRows;
    }
}
