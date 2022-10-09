package com.commerzinfo.util;

import com.commerzinfo.DataRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.tinylog.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExcelUtil {

    private ExcelUtil() {
    }

    public static void writeWorkbookToFile(File inFile, Workbook wb) throws IOException {
        File excelFile = createExcelFile(inFile);

        try (FileOutputStream fileOutputStream = new FileOutputStream(excelFile)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                wb.write(bufferedOutputStream);
                Logger.info("WRITING OF FILE " + excelFile.getName() + " was successful");
            }
        }
    }

    private static File createExcelFile(File inFile) throws IOException {
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

    private static Cell createCell(Row row, AtomicInteger cellCounter) {
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
        cell.setCellValue(dataRow.getValue().doubleValue());

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellValue(dataRow.getBookingText());
    }
}
