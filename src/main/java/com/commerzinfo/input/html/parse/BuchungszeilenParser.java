package com.commerzinfo.input.html.parse;

import com.commerzinfo.DataRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuchungszeilenParser {

    private BuchungszeilenParser() {
    }

    public static List<DataRow> parseRows(Collection<String> inputRows) {
        List<DataRow> buchungsZeilen = new ArrayList<>();

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
