package com.commerzinfo;

import org.apache.commons.io.FileUtils;
import org.tinylog.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryCollection {

    private static final String EQUALS = "=";

    private CategoryCollection() {
    }

    private static final String CATCHALL = "catchall";
    private static final Matcher whitespaceMatcher = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE).matcher("");
    private static final HashMap<String, Matcher> categoryMap = new LinkedHashMap<>();

    public static void createCategories(File configFile) {
        if (configFile != null && configFile.isFile()) {
            categoryMap.clear();
            try {
                for (String line : FileUtils.readLines(configFile, StandardCharsets.UTF_8)) {
                    if (!line.contains(EQUALS))
                        continue;
                    final String[] split = line.split(EQUALS);
                    if (split.length == 2) {
                        String catName = split[0];
                        String regex = split[1];

                        Matcher matcher = getMatcher(regex);
                        categoryMap.put(catName, matcher);
                    } else
                        throw new IllegalArgumentException("split error line=" + line + " split=" + EQUALS);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("configFile=" + configFile.getAbsolutePath(), e);
            }
        }

        categoryMap.put(CATCHALL, Pattern.compile(".*", Pattern.CASE_INSENSITIVE).matcher(""));
    }

    private static Matcher getMatcher(String regex) {
        whitespaceMatcher.reset(regex);
        if (whitespaceMatcher.find()) {
            String oldValue = regex;
            regex = whitespaceMatcher.replaceAll("\\\\s+");
            regex = regex.trim();
            Logger.info(String.format("replacing whitespaces %s=>%s", oldValue, regex));
        }
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher("");
    }

    public static List<String> getAllCategoryNames() {
        return new ArrayList<>(categoryMap.keySet());
    }

    public static Map<String, Collection<DataRow>> matchRowsToCategories(Collection<DataRow> parsedRows) {
        Map<String, Collection<DataRow>> categoryToRowMap = new HashMap<>();

        for (DataRow parsedRow : parsedRows) {
            for (Map.Entry<String, Matcher> entry : categoryMap.entrySet()) {
                final Matcher matcher = entry.getValue().reset(parsedRow.getBookingText());
                if (matcher.find()) {
                    String key = entry.getKey();
                    Collection<DataRow> rowCollection = categoryToRowMap.getOrDefault(key, new ArrayList<>());
                    rowCollection.add(parsedRow);

                    categoryToRowMap.put(key, rowCollection);
                    break;
                }
            }
        }
        return categoryToRowMap;
    }
}
