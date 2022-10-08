package com.commerzinfo;

import com.commerzinfo.input.csv.CSVParser;
import com.commerzinfo.input.html.HTMLParser;
import com.commerzinfo.output.AnotherExcelWriter;
import com.commerzinfo.util.FileCompressor;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.tinylog.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Launcher {

    public static void main(String[] args) throws Exception {
        try {
            ArgumentParser parser = ArgumentParsers.newFor("cominfo").build()
                    .defaultHelp(true)
                    .description("parses com html + csv and exports them to excel");
            parser.addArgument("-c", "--config")
                    .required(true)
                    .help("path where config file is located");
            parser.addArgument("folders").nargs("*")
                    .help("folders to recursively process");

            Namespace ns = parseArguments(args, parser);

            File configFile = new File(ns.getString("config"));
            CategoryCollection.createCategories(configFile); //init

            List<File> fileList = new ArrayList<>();
            List<String> folders = ns.get("folders");
            for (String s : folders) {
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

    private static Namespace parseArguments(String[] args, ArgumentParser parser) {
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return null;
    }
}
