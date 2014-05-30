package com.commerzinfo.categorize;

import com.commerzinfo.data.DataRow;
import com.google.common.base.Predicate;

public class Category {
    private String categoryName;
    private Predicate<DataRow> predicate;

    public Category(String categoryName, Predicate<DataRow> predicate) {
        this.categoryName = categoryName;
        this.predicate = predicate;
    }

    public Predicate<DataRow> getPredicate() {
        return predicate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isMatch(DataRow dataRow) {
        return predicate.apply(dataRow);
    }
}
