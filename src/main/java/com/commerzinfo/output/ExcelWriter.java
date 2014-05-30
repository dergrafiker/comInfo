package com.commerzinfo.output;

import com.commerzinfo.categorize.CategoryCollection;
import com.commerzinfo.categorize.CategoryMatcher;
import com.commerzinfo.data.DataRow;
import com.google.common.collect.Multimap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExcelWriter.class);

    public static void writeParsedRowsToFile(File file, Collection<DataRow> parsedRows) throws IOException {
        Multimap<String, DataRow> catToRows = CategoryMatcher.matchRowsToCategories(parsedRows);
        Workbook wb = new HSSFWorkbook();
        createSheet(wb, "data", catToRows);
        ExcelUtil.writeWorkbookToFile(file, wb);
    }

    private static Sheet createSheet(Workbook wb, String sheetName, Multimap<String, DataRow> catToRows) {
        Sheet sheet = wb.createSheet(sheetName);
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("d.m.yy"));
        Collection<String> categoryNames = CategoryCollection.getAllCategoryNames();
        AtomicInteger rowCounter = new AtomicInteger(0);

        for (String categoryName : categoryNames) {
            Collection<DataRow> rows = catToRows.get(categoryName);
            if (!rows.isEmpty()) {
                for (DataRow dataRow : rows) {
                    createDataRow(sheet, dateCellStyle, rowCounter, categoryName, dataRow);
                }
                ExcelUtil.createEmptyRows(sheet, rowCounter, 1);
            }
        }

        int lastRowNumAfterDataRows = sheet.getLastRowNum() + 1;
        ExcelUtil.createEmptyRows(sheet, rowCounter, 1);
        createSumIfBlock(categoryNames, sheet, rowCounter, lastRowNumAfterDataRows);
        createSumAtEnd(sheet, rowCounter, lastRowNumAfterDataRows);
        return sheet;
    }

    private static void createSumAtEnd(Sheet sheet, AtomicInteger rowCounter, int lastRowNum) {
        int lastRowNumAfterSumif = sheet.getLastRowNum() + 1;

        Row row = ExcelUtil.createRow(sheet, rowCounter);
        AtomicInteger cellCounter = new AtomicInteger(0);
        Cell cell;

        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellValue("saldo");

        String formula = String.format("SUM(%s)", String.format("B%s:B%s", lastRowNum, lastRowNumAfterSumif));
        cell = ExcelUtil.createCell(row, cellCounter);
        cell.setCellFormula(formula);
    }

    private static void createSumIfBlock(Collection<String> categoryNames, Sheet sheet, AtomicInteger rowCounter, int lastRowNum) {
        String criteriaRange = String.format("A1:A%s", lastRowNum);
        String sumRange = String.format("C1:C%s", lastRowNum);

        for (String categoryName : categoryNames) {
            Row row = ExcelUtil.createRow(sheet, rowCounter);
            AtomicInteger cellCounter = new AtomicInteger(0);
            Cell cell;

            cell = ExcelUtil.createCell(row, cellCounter);
            cell.setCellValue(categoryName);

            String formula = String.format("SUMIF(%s,A%s,%s)", criteriaRange, rowCounter.get(), sumRange);
            cell = ExcelUtil.createCell(row, cellCounter);
            cell.setCellFormula(formula);
        }
    }

    private static void createDataRow(Sheet sheet, CellStyle dateCellStyle, AtomicInteger rowCounter, String categoryName, DataRow dataRow) {
        Row row = ExcelUtil.createRow(sheet, rowCounter);
        AtomicInteger cellCounter = new AtomicInteger(0);
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
