package com.commerzinfo.input.csv;

import com.commerzinfo.DataRow;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CSVParserTest {
    @Test
    public void handleCSV() throws Exception {
        File inputFile = Paths.get("src/test/resources/testdata.CSV").toFile();
        List<DataRow> dataRows = CSVParser.handleCSV(inputFile);
        assertThat(dataRows.size(), is(4));
    }

}