package com.commerzinfo.util;

import com.google.common.collect.Lists;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class FileCompressor {
    public static List<File> compressFiles(Collection<File> inputFiles, String fileSuffix) throws IOException {
        List<File> fileCollection = Lists.newArrayList();

        for (File inputFile : inputFiles) {
            if (CompressionUtil.isCompressed(inputFile)) {
                fileCollection.add(inputFile);
            } else {
                File outFile = compressFile(inputFile, fileSuffix);
                if (!CompressionUtil.contentIsEqual(inputFile, outFile)) {
                    throw new RuntimeException("Problem with compressed Stream. check files!");
                }
                inputFile.deleteOnExit();
                fileCollection.add(outFile);
            }
        }
        return fileCollection;
    }

    private static File compressFile(File inputFile, String fileSuffix) throws IOException {
        String newFileName = inputFile.getName() + fileSuffix;
        File outFile = new File(inputFile.getParent(), newFileName);
        try (InputStream fis = IOUtils.toBufferedInputStream(new FileInputStream(inputFile));) {
            try (BZip2CompressorOutputStream bcos = new BZip2CompressorOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
                IOUtils.copy(fis, bcos);
            }
        }
        return outFile;
    }
}
