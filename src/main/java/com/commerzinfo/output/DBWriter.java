package com.commerzinfo.output;

import com.commerzinfo.CategoryCollection;
import com.commerzinfo.DataRow;
import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.h2.generated.tables.Category;
import org.jooq.h2.generated.tables.Datarow;
import org.jooq.h2.generated.tables.records.CategoryRecord;
import org.jooq.h2.generated.tables.records.DatarowRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;

public class DBWriter {
    private static Logger logger = LoggerFactory.getLogger(DBWriter.class);

    public static void assignCategories(DSLContext dsl) {
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

    public static void buildCategories(DSLContext dsl) {
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

    public static void insertRowsIntoDB(Collection<DataRow> parsedRows, DSLContext dsl) {
        int insertCount = 0, duplicateCount = 0;
        for (DataRow p : parsedRows) {
            try {
                dsl.insertInto(Datarow.DATAROW)
                        .set(Datarow.DATAROW.BOOKING_VALUE, p.getValue())
                        .set(Datarow.DATAROW.BOOKING_TEXT, p.getBookingText())
                        .set(Datarow.DATAROW.BOOKING_DATE, new Date(p.getBookingDate().getTime()))
                        .set(Datarow.DATAROW.VALUE_DATE, new Date(p.getValueDate().getTime())).execute();
                insertCount++;
            } catch (DataAccessException dae) {
                boolean isDuplicate = StringUtils.contains(dae.getMessage(), "Unique index or primary key violation");
                if (isDuplicate) {
                    logger.trace("duplicate row: {}", p);
                    duplicateCount++;
                } else
                    throw dae;
            }
        }
        logger.info("has {} inserts and {} duplicates", insertCount, duplicateCount);
    }
}
