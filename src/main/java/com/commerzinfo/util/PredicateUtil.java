package com.commerzinfo.util;

import com.commerzinfo.data.DataRow;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Range;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public final class PredicateUtil {
    private PredicateUtil() {
    }

    public static final Function<DataRow, String> getText = new Function<DataRow, String>() {
        public String apply(@Nullable DataRow dataRow) {
            return (dataRow != null) ? dataRow.getBookingText() : "";
        }
    };

    public static final Function<DataRow, Double> getAmount = new Function<DataRow, Double>() {
        public Double apply(@Nullable DataRow dataRow) {
            return (dataRow != null) ? dataRow.getValue() : 0.0;
        }
    };


    public static Predicate<DataRow> findPatternPredicate(final String pattern) {
        return new Predicate<DataRow>() {
            private Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

            public boolean apply(@Nullable DataRow input) {
                return input != null && p.matcher(getText.apply(input)).find();
            }
        };
    }

    public static Predicate<DataRow> rangePredicate(final Range<Double> range) {
        return new Predicate<DataRow>() {
            public boolean apply(@Nullable DataRow input) {
                return range.apply(getAmount.apply(input));
            }
        };
    }
}
