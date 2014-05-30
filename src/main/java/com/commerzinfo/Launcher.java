package com.commerzinfo;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.commerzinfo.categorize.CategoryCollection;
import com.commerzinfo.data.CSVBean;
import com.commerzinfo.data.DataRow;
import com.commerzinfo.input.FileHandler;
import com.commerzinfo.input.HTMLReader;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.parse.BuchungszeilenParser;
import com.commerzinfo.util.CompressionUtil;
import com.commerzinfo.util.FileCompressor;
import net.htmlparser.jericho.HTMLElementName;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Launcher {
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        try {
            MyOptions myOptions = new MyOptions();
            CmdLineParser parser = new CmdLineParser(myOptions);
            parser.parseArgument(args);

            CategoryCollection.createCategories(myOptions.getConfigFile()); //init

            List<File> fileList;
            fileList = FileHandler.getFiles(myOptions.getArguments(), myOptions.isRecursive());
            fileList = FileCompressor.compressFiles(fileList, ".bz2");
            Collections.sort(fileList, Collections.reverseOrder());
            String elementToSearch = HTMLElementName.SPAN;

            for (File file : fileList) {
                Collection<DataRow> parsedRows = Collections.EMPTY_LIST;

                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    Collection<String> elementsFromFile = HTMLReader.getElementsFromFile(file, elementToSearch);
                    logger.info(file + " has " + elementsFromFile.size() + " elements of type: " + elementToSearch);
                    parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
                    logger.info(file + " has " + parsedRows.size() + " parsed rows");
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(CompressionUtil.getCorrectInputStream(file), "UTF-8"), ';', '"');
                    HeaderColumnNameTranslateMappingStrategy<CSVBean> strat = new HeaderColumnNameTranslateMappingStrategy<CSVBean>();
                    strat.setType(CSVBean.class);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Buchungstag", "buchungstag"); //TODO remove BOM
                    map.put("Wertstellung", "wertstellung");
                    map.put("Buchungstext", "buchungstext");
                    map.put("Betrag", "betrag");
                    map.put("Währung", "waehrung");
                    strat.setColumnMapping(map);

                    CsvToBean<CSVBean> csv = new CsvToBean<CSVBean>();
                    List<CSVBean> list = csv.parse(strat, csvReader);

                    System.out.println();
                }

//                ExcelWriter.writeParsedRowsToFile(file, parsedRows);
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }
        } catch (Exception e) {
            logger.error("an error occurred while launching the program", e);
            throw e;
        }
    }
}
