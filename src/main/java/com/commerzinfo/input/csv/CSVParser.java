package com.commerzinfo.input.csv;

import com.commerzinfo.DataRow;
import com.commerzinfo.util.CompressionUtil;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.DecimalFormatUtil;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import org.apache.commons.io.input.BOMInputStream;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    public static List<DataRow> handleCSV(File file) throws IOException {
        List<DataRow> dataRows = new ArrayList<>();
        try (InputStream inputStream = CompressionUtil.getCorrectInputStream(file)) {
            try (BOMInputStream bomInputStream = new BOMInputStream(inputStream, false)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream, "UTF-8")) {
                    try (NamedCsvReader csvReader = NamedCsvReader.builder()
                            .fieldSeparator(';')
                            .quoteCharacter('"')
                            .build(inputStreamReader)) {
                        csvReader.forEach(csvRow -> {
                            try {
                                DataRow row = new DataRow();
                                row.setBookingDate(DateUtil.parse(csvRow.getField("Buchungstag")));
                                row.setValueDate(DateUtil.parse(csvRow.getField("Wertstellung")));
                                row.setBookingText(csvRow.getField("Buchungstext"));
                                row.setValue((java.math.BigDecimal) DecimalFormatUtil.parse(csvRow.getField("Betrag"),
                                        DecimalFormatUtil.Mode.CSV));
                                dataRows.add(row);
                            } catch (Exception e) {
                                Logger.error("problem with datarow mapping", e);
                            }
                        });
                    }
                }
            }
        }
        Logger.info("{} has {} parsed rows", file.getAbsolutePath(), dataRows.size());
        return dataRows;
    }
}
