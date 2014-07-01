package com.commerzinfo.categorize;

import com.commerzinfo.data.DataRow;
import com.commerzinfo.util.PredicateUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryCollection {
    public static final String CATCHALL = "catchall";
    public static final Function<Category, String> CATEGORY_STRING_FUNCTION = new Function<Category, String>() {
        @Override
        public String apply(@Nullable Category input) {
            return (input != null) ? input.getCategoryName() : "";
        }
    };
    private static final Collection<Category> allCategories = new ArrayList<Category>();
    private static final Matcher whitespaceMatcher = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE).matcher("");
    private static final LinkedHashMap<String, Matcher> categoryMap = Maps.newLinkedHashMap();
    private static Logger logger = LoggerFactory.getLogger(CategoryCollection.class);

    @SuppressWarnings("unchecked")
    public static Collection<Category> createCategories(File configFile) {
        allCategories.clear();

        if (configFile != null && configFile.isFile()) {
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
                        addCat(allCategories, catName, PredicateUtil.findPatternPredicate(regex));
                    } else
                        throw new RuntimeException("split error");
                }
            } catch (Exception e) {
                throw new RuntimeException("configFile=" + configFile.getAbsolutePath(), e);
            }
        }

        addCat(allCategories, CATCHALL, Predicates.<DataRow>alwaysTrue());
        categoryMap.put(CATCHALL, Pattern.compile(".*", Pattern.CASE_INSENSITIVE).matcher(""));
        return allCategories;
    }

    private static void addCat(Collection<Category> categoryCollection, String categoryName, Predicate<DataRow> predicate) {
        categoryCollection.add(new Category(categoryName, predicate));
    }

    public static Collection<Category> getAllCategories() {
        return allCategories;
    }

    public static Collection<String> getAllCategoryNames() {
        return Collections2.transform(allCategories, CATEGORY_STRING_FUNCTION);
    }

    public static LinkedHashMap<String, Matcher> getCategoryMap() {
        return categoryMap;
    }
}
