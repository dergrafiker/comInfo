package com.commerzinfo;

import com.commerzinfo.categorize.CategoryCollection;
import com.commerzinfo.data.DataRow;
import com.commerzinfo.input.FileHandler;
import com.commerzinfo.input.HTMLReader;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.parse.BuchungszeilenParser;
import com.commerzinfo.util.FileCompressor;
import net.htmlparser.jericho.HTMLElementName;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                Collection<String> elementsFromFile = HTMLReader.getElementsFromFile(file, elementToSearch);
                logger.info(file + " has " + elementsFromFile.size() + " elements of type: " + elementToSearch);
                Collection<DataRow> parsedRows = BuchungszeilenParser.parseRows(elementsFromFile);
                logger.info(file + " has " + parsedRows.size() + " parsed rows");
//                ExcelWriter.writeParsedRowsToFile(file, parsedRows);
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }
        } catch (Exception e) {
            logger.error("an error occurred while launching the program", e);
            throw e;
        }
    }
}
