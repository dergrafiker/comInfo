package com.commerzinfo.input.html.parse;

import com.commerzinfo.DataRow;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class BuchungszeilenParser {

    public static List<DataRow> parseRows(Collection<String> inputRows) {
        List<DataRow> buchungsZeilen = Lists.newArrayList();

        ParseStateContext ctx = new ParseStateContext();

        for (String inputRow : inputRows) {
            DataRow buchungszeile = ctx.processInput(ctx, inputRow);
            if (buchungszeile != null) {
                buchungsZeilen.add(buchungszeile);
            }
        }

        return buchungsZeilen;
    }
}
