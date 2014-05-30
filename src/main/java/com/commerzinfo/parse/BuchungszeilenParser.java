package com.commerzinfo.parse;

import com.commerzinfo.data.DataRow;
import com.google.common.collect.Lists;

import java.util.Collection;

public class BuchungszeilenParser {

    public static Collection<DataRow> parseRows(Collection<String> inputRows) {
        Collection<DataRow> buchungsZeilen = Lists.newLinkedList();

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
