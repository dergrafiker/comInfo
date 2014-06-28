package com.commerzinfo;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.commerzinfo.categorize.CategoryCollection;
import com.commerzinfo.data.CSVBean;
import com.commerzinfo.data.DataRow;
import com.commerzinfo.input.HTMLReader;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.parse.BuchungszeilenParser;
import com.commerzinfo.util.CompressionUtil;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.FileCompressor;
import net.htmlparser.jericho.HTMLElementName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.h2.generated.tables.Category;
import org.jooq.h2.generated.tables.Datarow;
import org.jooq.h2.generated.tables.records.CategoryRecord;
import org.jooq.h2.generated.tables.records.DatarowRecord;
import org.jooq.impl.DSL;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

public class Launcher {
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        try {
            MyOptions myOptions = new MyOptions();
            CmdLineParser parser = new CmdLineParser(myOptions);
            if (args.length == 0) {
                parser.printUsage(System.out);
                System.exit(1);
            }
            parser.parseArgument(args);

            CategoryCollection.createCategories(myOptions.getConfigFile()); //init

            List<File> fileList = new ArrayList<File>();
            for (String s : myOptions.getArguments()) {
                fileList.addAll(FileUtils.listFiles(new File(s), Constants.ALLOWED_FILE_FILTER, TrueFileFilter.INSTANCE));
            }

            fileList = FileCompressor.compressFiles(fileList, ".bz2");
            Collections.sort(fileList, Collections.reverseOrder());
            String elementToSearch = HTMLElementName.SPAN;

            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/cominfo", "sa", "");
            DSLContext dsl = DSL.using(conn, SQLDialect.H2);

            for (File file : fileList) {
                Collection<DataRow> parsedRows = Collections.EMPTY_LIST;
                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    parsedRows = handleHTML(elementToSearch, file);
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    parsedRows = handleCSV(file);
                }

                insertRowsIntoDB(parsedRows, dsl);

//                ExcelWriter.writeParsedRowsToFile(file, parsedRows);
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }

            buildCategories(dsl);
            assignCategories(dsl);

            /*
            Query to check assignment
            SELECT C.NAME, D.BOOKING_TEXT  FROM COMINFO.DATAROW  AS D
JOIN COMINFO.CATEGORY AS C ON C.ID = D.CAT_ID
ORDER BY D.BOOKING_TEXT;
             */
        } catch (Exception e) {
            logger.error("an error occurred while launching the program", e);
            throw e;
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    private static void assignCategories(DSLContext dsl) {
        Cursor<DatarowRecord> cursor = dsl.selectFrom(Datarow.DATAROW).fetchLazy();

        try {
            while (cursor.hasNext()) {
                DatarowRecord datarowRecord = cursor.fetchOne();

                for (Map.Entry<String, Matcher> entry : CategoryCollection.getCategoryMap().entrySet()) {
                    Matcher m = entry.getValue().reset(datarowRecord.getBookingText());
                    if (m.find()) {
                        CategoryRecord categoryRecord = dsl.selectFrom(Category.CATEGORY)
                                .where(Category.CATEGORY.NAME.eq(entry.getKey())).fetchOne();
                        datarowRecord.setCatId(categoryRecord.getId());
                        datarowRecord.update();
                        break;
                    }
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private static void buildCategories(DSLContext dsl) {
        dsl.delete(org.jooq.h2.generated.tables.Category.CATEGORY).execute();

        for (Map.Entry<String, Matcher> entry : CategoryCollection.getCategoryMap().entrySet()) {
            String catName = entry.getKey();
            String regex = entry.getValue().pattern().pattern();

            dsl.insertInto(org.jooq.h2.generated.tables.Category.CATEGORY)
                    .set(Category.CATEGORY.NAME, catName)
                    .set(org.jooq.h2.generated.tables.Category.CATEGORY.REGEX, regex)
                    .execute();
        }
    }

    private static void insertRowsIntoDB(Collection<DataRow> parsedRows, DSLContext dsl) {
        for (DataRow p : parsedRows) {
            try {
                dsl.insertInto(Datarow.DATAROW)
                        .set(Datarow.DATAROW.BOOKING_VALUE, BigDecimal.valueOf(p.getValue()))
                        .set(Datarow.DATAROW.BOOKING_TEXT, p.getBookingText())
                        .set(Datarow.DATAROW.BOOKING_DATE, new Date(p.getBookingDate().getTime()))
                        .set(Datarow.DATAROW.VALUE_DATE, new Date(p.getValueDate().getTime())).execute();

            } catch (DataAccessException dae) {
                boolean isDuplicate = StringUtils.contains(dae.getMessage(), "Unique index or primary key violation");
                if (isDuplicate)
                    logger.warn("duplicate row: {}", p);
                else
                    throw dae;
            }
        }
    }

    private static Collection<DataRow> handleHTML(String elementToSearch, File file) throws IOException {
        Collection<DataRow> parsedRows;
        Collection<String> elementsFromFile = HTMLReader.getElementsFromFile(file, elementToSearch);
        logger.info(file + " has " + elementsFromFile.size() + " elements of type: " + elementToSearch);
        parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
        logger.info(file + " has " + parsedRows.size() + " parsed rows");
        return parsedRows;
    }

    private static Collection<DataRow> handleCSV(File file) throws IOException {
        Collection<DataRow> dataRows = new ArrayList<DataRow>();

        CSVReader csvReader = new CSVReader(new InputStreamReader(CompressionUtil.getCorrectInputStream(file), "UTF-8"), ';', '"');
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
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
        df.setNegativePrefix("-");
        df.setPositivePrefix("+");

        for (CSVBean csvBean : csvBeanList) {
            try {
                DataRow row = new DataRow();
                row.setBookingDate(DateUtil.parse(csvBean.getBuchungstag()));
                row.setValueDate(DateUtil.parse(csvBean.getWertstellung()));
                row.setBookingText(csvBean.getBuchungstext());
                row.setValue(df.parse(csvBean.getBetrag()).doubleValue());
                dataRows.add(row);
            } catch (ParseException e) {
                logger.error("problem with datarow mapping", e);
            }

        }
        return dataRows;
    }
}
