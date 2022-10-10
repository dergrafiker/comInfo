package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.Constants;
import com.commerzinfo.input.html.parse.ParseStateContext;
import com.commerzinfo.util.RegexUtil;

import java.text.ParseException;
import java.time.LocalDate;

public class StateValuta extends ParseState {
    public StateValuta() {
        super(StateValuta.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return RegexUtil.matchesDate(input);
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws ParseException {
        stateContext.getBuchungszeile().setValueDate(LocalDate.parse(input, Constants.DDMMYYYY));
        stateContext.setState(ParseStateContext.BETRAG_STATE);
    }
}
