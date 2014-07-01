package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.input.html.parse.ParseStateContext;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.RegexUtil;

public class StateBuchungstag extends ParseState {
    public StateBuchungstag() {
        super(StateBuchungstag.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return RegexUtil.matchesDate(input);
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws Exception {
        stateContext.getBuchungszeile().setBookingDate(DateUtil.parse(input));
        stateContext.setState(ParseStateContext.BUCHUNGSTEXT_STATE);
    }
}
