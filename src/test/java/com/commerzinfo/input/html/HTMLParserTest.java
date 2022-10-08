package com.commerzinfo.input.html;

import com.commerzinfo.DataRow;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HTMLParserTest {
    @Test
    public void handleHTML() throws Exception {
        File inputFile = Paths.get("src/test/resources/testdata.html").toFile();
        List<DataRow> dataRows = HTMLParser.getAllElementTextsFromFile("span", inputFile);
        assertThat(dataRows.size(), is(2));
    }

}