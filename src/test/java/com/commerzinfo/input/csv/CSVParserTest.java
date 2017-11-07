package com.commerzinfo.input.csv;

import com.commerzinfo.DataRow;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CSVParserTest {
    @Test
    public void handleCSV() throws Exception {
        File inputFile = Paths.get("src/test/resources/testdata.CSV").toFile();
        List<DataRow> dataRows = CSVParser.handleCSV(inputFile);
        Assert.assertThat(dataRows.size(), is(4));
    }

}