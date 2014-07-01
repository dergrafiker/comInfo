package com.commerzinfo;

import com.commerzinfo.data.DataRow;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryCollection {
    public static final String CATCHALL = "catchall";
    private static final Matcher whitespaceMatcher = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE).matcher("");
    private static final LinkedHashMap<String, Matcher> categoryMap = Maps.newLinkedHashMap();
    private static Logger logger = LoggerFactory.getLogger(CategoryCollection.class);

    @SuppressWarnings("unchecked")
    public static Map<String, Matcher> createCategories(File configFile) {
        if (configFile != null && configFile.isFile()) {
            categoryMap.clear();
            try {
                for (String line : FileUtils.readLines(configFile, "UTF-8")) {
                    if (!line.contains("="))
                        continue;
                    final String[] split = line.split("=");
                    if (split.length == 2) {
                        String catName = split[0];
                        String regex = split[1];

                        whitespaceMatcher.reset(regex);
                        if (whitespaceMatcher.find()) {
                            String oldValue = regex;
                            regex = whitespaceMatcher.replaceAll("\\\\s+");
                            regex = regex.trim();
                            logger.info("replacing whitespaces " + oldValue + "=>" + regex);
                        }
                        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher("");
                        categoryMap.put(catName, matcher);
                    } else
                        throw new RuntimeException("split error");
                }
            } catch (Exception e) {
                throw new RuntimeException("configFile=" + configFile.getAbsolutePath(), e);
            }
        }

        categoryMap.put(CATCHALL, Pattern.compile(".*", Pattern.CASE_INSENSITIVE).matcher(""));
        return categoryMap;
    }

    public static List<String> getAllCategoryNames() {
        return Lists.newArrayList(categoryMap.keySet());
    }

    public static LinkedHashMap<String, Matcher> getCategoryMap() {
        return categoryMap;
    }

    public static Multimap<String, DataRow> matchRowsToCategories(Collection<DataRow> parsedRows) {
        Multimap<String, DataRow> categoryToRowMap = ArrayListMultimap.create();

        for (DataRow parsedRow : parsedRows) {
            for (Map.Entry<String, Matcher> entry : categoryMap.entrySet()) {
                final Matcher matcher = entry.getValue().reset(parsedRow.getBookingText());
                if (matcher.find()) {
                    categoryToRowMap.put(entry.getKey(), parsedRow);
                    break;
                }
            }
        }
        return categoryToRowMap;
    }
}
