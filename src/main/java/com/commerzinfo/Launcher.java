package com.commerzinfo;

import com.commerzinfo.input.csv.CSVParser;
import com.commerzinfo.input.html.HTMLParser;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.util.FileCompressor;
import net.htmlparser.jericho.HTMLElementName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

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
                logger.info("READING FILE {}", file.getAbsolutePath());

                List<DataRow> parsedRows = new ArrayList<>();
                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    parsedRows = HTMLParser.handleHTML(HTMLElementName.SPAN, file);
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    parsedRows = CSVParser.handleCSV(file);
                }
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }
        } catch (Exception e) {
            logger.error("an error occurred while launching the program", e);
            throw e;
        }
    }
}
