package com.commerzinfo;

import com.commerzinfo.input.csv.CSVParser;
import com.commerzinfo.input.html.HTMLParser;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.output.DBWriter;
import com.commerzinfo.util.FileCompressor;
import com.google.common.collect.Lists;
import net.htmlparser.jericho.HTMLElementName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

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
            conn = DriverManager.getConnection("jdbc:h2:~/cominfo;AUTO_SERVER=TRUE", "sa", "");
            DSLContext dsl = DSL.using(conn, SQLDialect.H2);

            for (File file : fileList) {
                List<DataRow> parsedRows = Lists.newArrayList();
                if (Constants.HTML_FILE_FILTER.accept(file)) {
                    parsedRows = HTMLParser.handleHTML(HTMLElementName.SPAN, file);
                } else if (Constants.CSV_FILE_FILTER.accept(file)) {
                    parsedRows = CSVParser.handleCSV(file);
                }

                DBWriter.insertRowsIntoDB(parsedRows, dsl);
                AnotherExcelWriter.writeParsedRowsToFile(file, parsedRows);
            }

            DBWriter.buildCategories(dsl);
            DBWriter.assignCategories(dsl);
        } catch (Exception e) {
            logger.error("an error occurred while launching the program", e);
            throw e;
        } finally {
            if (conn != null)
                conn.close();
        }
    }
}
