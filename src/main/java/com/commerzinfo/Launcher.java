package com.commerzinfo;

import com.commerzinfo.input.csv.CSVParser;
import com.commerzinfo.input.html.HTMLParser;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.util.FileCompressor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.kohsuke.args4j.CmdLineParser;
import org.tinylog.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Launcher {

    public static void main(String[] args) throws Exception {
        try {
            MyOptions myOptions = new MyOptions();
            CmdLineParser parser = new CmdLineParser(myOptions);
            if (args.length == 0) {
                parser.printUsage(System.out);
                System.exit(1);
            }
            parser.parseArgument(args);

            CategoryCollection.createCategories(myOptions.getConfigFile()); //init

            List<File> fileList = new ArrayList<>();
            for (String s : myOptions.getArguments()) {
                fileList.addAll(FileUtils.listFiles(new File(s), Constants.ALLOWED_FILE_FILTER, TrueFileFilter.INSTANCE));
            }

            fileList = FileCompressor.compressFiles(fileList, ".bz2");
            fileList.sort(Collections.reverseOrder());

            for (File file : fileList) {
                Logger.info("READING FILE {}", file.getAbsolutePath());

                List<DataRow> parsedRows = new ArrayList<>();
                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    parsedRows = HTMLParser.getAllElementTextsFromFile("span", file);
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    parsedRows = CSVParser.handleCSV(file);
                }
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }
        } catch (Exception e) {
            Logger.error("an error occurred while launching the program", e);
            throw e;
        }
    }
}
