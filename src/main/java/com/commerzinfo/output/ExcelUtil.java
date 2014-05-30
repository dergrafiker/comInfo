package com.commerzinfo.output;

import com.commerzinfo.data.DataRow;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExcelUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    private ExcelUtil() {
    }

    public static void writeWorkbookToFile(File inFile, Workbook wb) throws IOException {
        File excelFile = createExcelFile(inFile);
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(excelFile);
            wb.write(fileOut);
            if (logger.isInfoEnabled()) {
                logger.info("WRITING OF FILE " + excelFile.getName() + " was succesful");
            }
        } finally {
            IOUtils.closeQuietly(fileOut);
        }
    }

    public static File createExcelFile(File inFile) throws IOException {
        File parentFile = inFile.getParentFile();
        String newFilename = inFile.getName() + ".xls";

        File excelFile = new File(parentFile, newFilename);
        excelFile.createNewFile();

        return excelFile;
    }

    public static void createEmptyRows(Sheet sheet, AtomicInteger rowCounter, int count) {
        for (int i = 0; i < count; i++) {
            createRow(sheet, rowCounter);
        }
    }

    public static Row createRow(Sheet sheet, AtomicInteger rowCounter) {
        Row row = sheet.getRow(rowCounter.get());

        if (row == null) {
            row = sheet.createRow(rowCounter.get());
        }

        rowCounter.incrementAndGet();
        return row;
    }

    public static Cell createCell(Row row, AtomicInteger cellCounter) {
        Cell cell = row.createCell(cellCounter.get());
        cellCounter.incrementAndGet();
        return cell;
    }

    public static void createDataRow(Sheet sheet, CellStyle dateCellStyle, AtomicInteger rowCounter, int startColIdx, String categoryName, DataRow dataRow) {
        Row row = ExcelUtil.createRow(sheet, rowCounter);
        AtomicInteger cellCounter = new AtomicInteger(startColIdx);
        Cell cell;

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellValue(categoryName);

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellStyle(dateCellStyle);
        cell.setCellValue(dataRow.getBookingDate());

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellValue(dataRow.getValue());

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellValue(dataRow.getBookingText());
    }
}
