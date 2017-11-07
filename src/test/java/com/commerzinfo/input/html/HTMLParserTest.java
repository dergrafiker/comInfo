package com.commerzinfo.input.html;

import com.commerzinfo.DataRow;
import com.commerzinfo.input.csv.CSVParser;
import net.htmlparser.jericho.HTMLElementName;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HTMLParserTest {
    @Test
    public void handleHTML() throws Exception {
        File inputFile = Paths.get("src/test/resources/testdata.html").toFile();
        List<DataRow> dataRows = HTMLParser.handleHTML(HTMLElementName.SPAN, inputFile);
        Assert.assertThat(dataRows.size(), is(2));
    }

}