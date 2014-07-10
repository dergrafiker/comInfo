package com.commerzinfo.util;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class CompressionUtil {
    private CompressionUtil() {
    }

    public static InputStream getCorrectInputStream(File inputFile) throws IOException {
        InputStream fis = IOUtils.toBufferedInputStream(new FileInputStream(inputFile));
        if (GzipUtils.isCompressedFilename(inputFile.getName())) {
            fis = new GzipCompressorInputStream(fis, true);
        } else if (BZip2Utils.isCompressedFilename(inputFile.getName())) {
            fis = new BZip2CompressorInputStream(fis, true);
        }
        return fis;
    }

    public static boolean isCompressed(File inputFile) {
        String name = inputFile.getName();
        return BZip2Utils.isCompressedFilename(name) || GzipUtils.isCompressedFilename(name);
    }

    public static boolean contentIsEqual(File one, File another) throws IOException {
        byte[] inBytes = IOUtils.toByteArray(getCorrectInputStream(one));
        byte[] outBytes = IOUtils.toByteArray(getCorrectInputStream(another));
        return ArchiveUtils.isEqual(inBytes, outBytes);
    }
}
