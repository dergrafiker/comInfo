package com.commerzinfo.input.csv;

import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.commerzinfo.DataRow;
import com.commerzinfo.util.CompressionUtil;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.DecimalFormatUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    private static Logger logger = LoggerFactory.getLogger(CSVParser.class);

    public static List<DataRow> handleCSV(File file) throws IOException {
        List<DataRow> dataRows = Lists.newArrayList();

        au.com.bytecode.opencsv.CSVReader csvReader = new au.com.bytecode.opencsv.CSVReader(new InputStreamReader(CompressionUtil.getCorrectInputStream(file), "UTF-8"), ';', '"');
        HeaderColumnNameTranslateMappingStrategy<CSVBean> strat = new HeaderColumnNameTranslateMappingStrategy<CSVBean>();
        strat.setType(CSVBean.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Buchungstag", "buchungstag");
        map.put("Wertstellung", "wertstellung");
        map.put("Buchungstext", "buchungstext");
        map.put("Betrag", "betrag");
        map.put("Währung", "waehrung");
        strat.setColumnMapping(map);

        CsvToBean<CSVBean> csv = new CsvToBean<CSVBean>();
        List<CSVBean> csvBeanList = csv.parse(strat, csvReader);

        for (CSVBean csvBean : csvBeanList) {
            try {
                DataRow row = new DataRow();
                row.setBookingDate(DateUtil.parse(csvBean.getBuchungstag()));
                row.setValueDate(DateUtil.parse(csvBean.getWertstellung()));
                row.setBookingText(csvBean.getBuchungstext());
                row.setValue((java.math.BigDecimal) DecimalFormatUtil.parse(csvBean.getBetrag(), DecimalFormatUtil.Mode.CSV));
                dataRows.add(row);
            } catch (ParseException e) {
                logger.error("problem with datarow mapping", e);
            }
        }
        logger.info("{} has {} parsed rows", file.getAbsolutePath(), dataRows.size());
        return dataRows;
    }
}
