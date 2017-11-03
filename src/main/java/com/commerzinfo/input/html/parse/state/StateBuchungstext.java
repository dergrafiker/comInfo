package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.input.html.parse.ParseStateContext;
import org.apache.commons.lang3.StringUtils;

public class StateBuchungstext extends ParseState {
    public StateBuchungstext() {
        super(StateBuchungstext.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return StringUtils.isNotBlank(input);
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws Exception {
        stateContext.getBuchungszeile().setBookingText(input);
        stateContext.setState(ParseStateContext.VALUTA_STATE);
    }
}
