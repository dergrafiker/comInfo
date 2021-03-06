package com.commerzinfo.input.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.commerzinfo.DataRow;
import com.commerzinfo.util.CompressionUtil;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.DecimalFormatUtil;
import com.google.common.collect.Lists;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    private static final MappingStrategy<CSVBean> mappingStrategy = initStrategy();
    private static final Logger logger = LoggerFactory.getLogger(CSVParser.class);
    private static final char SEPARATOR = ';';
    private static final char QUOTECHAR = '"';

    private static HeaderColumnNameTranslateMappingStrategy<CSVBean> initStrategy() {
        final HeaderColumnNameTranslateMappingStrategy<CSVBean> translateMappingStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
        translateMappingStrategy.setType(CSVBean.class);
        Map<String, String> map = new HashMap<>();
        map.put("Buchungstag", "buchungstag");
        map.put("Wertstellung", "wertstellung");
        map.put("Buchungstext", "buchungstext");
        map.put("Betrag", "betrag");
        map.put("Währung", "waehrung");
        translateMappingStrategy.setColumnMapping(map);
        return translateMappingStrategy;
    }

    public static List<DataRow> handleCSV(File file) throws IOException {
        List<DataRow> dataRows = Lists.newArrayList();
        try (InputStream inputStream = CompressionUtil.getCorrectInputStream(file)) {
            try (BOMInputStream bomInputStream = new BOMInputStream(inputStream, false)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream, "UTF-8")) {
                    List<CSVBean> csvBeanList = new CsvToBeanBuilder<CSVBean>(inputStreamReader)
                            .withSeparator(SEPARATOR)
                            .withQuoteChar(QUOTECHAR)
                            .withMappingStrategy(mappingStrategy)
                            .build()
                            .parse();

                    for (CSVBean csvBean : csvBeanList) {
                        try {
                            DataRow row = new DataRow();
                            row.setBookingDate(DateUtil.parse(csvBean.getBuchungstag()));
                            row.setValueDate(DateUtil.parse(csvBean.getWertstellung()));
                            row.setBookingText(csvBean.getBuchungstext());
                            row.setValue((java.math.BigDecimal) DecimalFormatUtil.parse(csvBean.getBetrag(),
                                                                                        DecimalFormatUtil.Mode.CSV));
                            dataRows.add(row);
                        } catch (ParseException e) {
                            logger.error("problem with datarow mapping", e);
                        }
                    }
                }
            }
        }
        logger.info("{} has {} parsed rows", file.getAbsolutePath(), dataRows.size());
        return dataRows;
    }
}
