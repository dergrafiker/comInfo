package com.commerzinfo;

import com.commerzinfo.data.DataRow;
import com.commerzinfo.input.csv.CSVParser;
import com.commerzinfo.input.html.HTMLParser;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.util.FileCompressor;
import com.google.common.collect.Lists;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

            List<File> fileList = Lists.newArrayList();
            for (String s : myOptions.getArguments()) {
                fileList.addAll(FileUtils.listFiles(new File(s), Constants.ALLOWED_FILE_FILTER, TrueFileFilter.INSTANCE));
            }

            fileList = FileCompressor.compressFiles(fileList, ".bz2");
            Collections.sort(fileList, Collections.reverseOrder());

            Class.forName(org.h2.Driver.class.getName());
            conn = DriverManager.getConnection("jdbc:h2:~/cominfo", "sa", "");
            DSLContext dsl = DSL.using(conn, SQLDialect.H2);

            for (File file : fileList) {
                List<DataRow> parsedRows = Lists.newArrayList();
                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    parsedRows = HTMLParser.handleHTML(HTMLElementName.SPAN, file);
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    parsedRows = CSVParser.handleCSV(file);
                }

                insertRowsIntoDB(parsedRows, dsl);
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
}
