package com.commerzinfo.parse.state;

import com.commerzinfo.parse.ParseStateContext;
import org.apache.commons.lang.StringUtils;

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
