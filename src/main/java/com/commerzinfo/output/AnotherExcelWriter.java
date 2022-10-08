package com.commerzinfo.output;

import com.commerzinfo.CategoryCollection;
import com.commerzinfo.DataRow;
import com.commerzinfo.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("SameParameterValue")
public class AnotherExcelWriter {
    public static void writeParsedRowsToFile(File file, Collection<DataRow> parsedRows) throws IOException {
        try (Workbook wb = new HSSFWorkbook()) {
            Sheet sheet = wb.createSheet("data");

            writeCategorySummary(sheet, "A1", "D:D", "F:F");
            writeDataRows(sheet, parsedRows, "D1");

            ExcelUtil.writeWorkbookToFile(file, wb);
        }
    }

    private static void writeDataRows(Sheet sheet, Collection<DataRow> parsedRows, String startRefString) {
        Map<String, Collection<DataRow>> catToRows = CategoryCollection.matchRowsToCategories(parsedRows);
        CellReference startRef = new CellReference(startRefString);
        AtomicInteger rowCounter = new AtomicInteger(startRef.getRow());

        Workbook wb = sheet.getWorkbook();
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("d.m.yy"));

        for (String categoryName : CategoryCollection.getAllCategoryNames()) {
            Collection<DataRow> rows = catToRows.getOrDefault(categoryName, Collections.emptySet());
            if (!rows.isEmpty()) {
                for (DataRow dataRow : rows) {
                    ExcelUtil.createDataRow(sheet, dateCellStyle, rowCounter, startRef.getCol(), categoryName, dataRow);
                }
                ExcelUtil.createEmptyRows(sheet, rowCounter, 1);
            }
        }
    }

    private static void writeCategorySummary(Sheet sheet, String startRefString, String criteriaRange, String sumRange) {
        Collection<String> categoryNames = CategoryCollection.getAllCategoryNames();
        CellReference startRef = new CellReference(startRefString);
        short col = startRef.getCol();
        AtomicInteger rowIndex = new AtomicInteger(startRef.getRow());

        for (String categoryName : categoryNames) {
            Row row = ExcelUtil.createRow(sheet, rowIndex);
            CellReference labelRef = new CellReference(row.getRowNum(), col);
            String searchCriteria = labelRef.formatAsString();
            String formula = String.format("SUMIF(%s,%s,%s)", criteriaRange, searchCriteria, sumRange);

            Cell cell;
            cell = row.createCell(col);
            cell.setCellValue(categoryName);
            cell = row.createCell(col + 1);
            cell.setCellFormula(formula);
        }

        CellReference sumStart = new CellReference(startRef.getRow(), col + 1);
        CellReference sumEnd = new CellReference(startRef.getRow() + categoryNames.size() - 1, col + 1);
        String formula = String.format("SUM(%s:%s)", sumStart.formatAsString(), sumEnd.formatAsString());

        Row summaryRow = ExcelUtil.createRow(sheet, rowIndex);
        Cell cell;
        cell = summaryRow.createCell(col);
        cell.setCellValue("saldo");
        cell = summaryRow.createCell(col + 1);
        cell.setCellFormula(formula);
    }
}
