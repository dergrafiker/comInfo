package com.commerzinfo.categorize;

import com.commerzinfo.data.DataRow;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class CategoryMatcher {

    public static Multimap<String, DataRow> matchRowsToCategories(Collection<DataRow> parsedRows) {
        Multimap<String, DataRow> categoryToRowMap = ArrayListMultimap.create();

        for (DataRow parsedRow : parsedRows) {
            for (Category category : CategoryCollection.getAllCategories()) {
                if (category.isMatch(parsedRow)) {
                    categoryToRowMap.put(category.getCategoryName(), parsedRow);
                    break;
                }
            }
        }
        return categoryToRowMap;
    }
}
