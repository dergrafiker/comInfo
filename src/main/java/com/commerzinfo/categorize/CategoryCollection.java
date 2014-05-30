package com.commerzinfo.categorize;

import com.commerzinfo.data.DataRow;
import com.commerzinfo.util.PredicateUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryCollection {
    private static Logger logger = LoggerFactory.getLogger(CategoryCollection.class);
    private static final Collection<Category> allCategories = new ArrayList<Category>();
    private static final Matcher whitespaceMatcher = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE).matcher("");

    public static final String CATCHALL = "catchall";
    public static final Function<Category, String> CATEGORY_STRING_FUNCTION = new Function<Category, String>() {
        @Override
        public String apply(@Nullable Category input) {
            return (input != null) ? input.getCategoryName() : "";
        }
    };

    @SuppressWarnings("unchecked")
    public static Collection<Category> createCategories(File configFile) {
        allCategories.clear();

        if (configFile != null && configFile.isFile()) {
            try {
                Properties properties = new LinkedProperties();
                properties.load(new FileReader(configFile));

                for (String propName : properties.stringPropertyNames()) {
                    String value = properties.getProperty(propName);

                    whitespaceMatcher.reset(value);
                    if (whitespaceMatcher.find()) {
                        String oldValue = value;
                        value = whitespaceMatcher.replaceAll("\\\\s+");
                        logger.info("replacing whitespaces " + oldValue + "=>" + value);
                    }
                    addCat(allCategories, propName, PredicateUtil.findPatternPredicate(value));
                }
            } catch (Exception e) {
                throw new RuntimeException("configFile=" + configFile.getAbsolutePath(), e);
            }
        }

        addCat(allCategories, CATCHALL, Predicates.<DataRow>alwaysTrue());
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
}
