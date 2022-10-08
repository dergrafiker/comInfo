package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.input.html.parse.ParseStateContext;

public class StateBuchungstext extends ParseState {
    public StateBuchungstext() {
        super(StateBuchungstext.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return input != null && !input.isBlank();
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws Exception {
        stateContext.getBuchungszeile().setBookingText(input);
        stateContext.setState(ParseStateContext.VALUTA_STATE);
    }
}
