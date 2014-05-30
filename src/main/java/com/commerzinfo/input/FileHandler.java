package com.commerzinfo.input;

import com.commerzinfo.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class FileHandler {
    public static List<File> getFiles(List<String> args, boolean isRecursive) {
        if (args.size() == 0)
            throw new IllegalArgumentException("No arguments given");

        List<File> files = Lists.newLinkedList();
        IOFileFilter dirFilter = (isRecursive) ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE;

        for (String arg : args) {
            File input = new File(arg);
            if (input.isFile()) {
                files.addAll(handleFile(input));
            } else if (input.isDirectory()) {
                files.addAll(handleDirectory(input, dirFilter));
            }
        }

        if (files.size() == 0)
            throw new IllegalArgumentException("no file matching " + StringUtils.join(Constants.ALLOWED_PATTERNS, ',') + " were found (recursion active: " + Boolean.toString(isRecursive) +
                    ")");

        return files;
    }

    private static Collection<File> handleDirectory(File input, IOFileFilter dirFilter) {
        return FileUtils.listFiles(input, Constants.ALLOWED_FILE_FILTER, dirFilter);
    }

    private static Collection<File> handleFile(File input) {
        Collection<File> files = Lists.newLinkedList();

        if (Constants.ALLOWED_FILE_FILTER.accept(input))
            files.add(input);

        return files;
    }
}
