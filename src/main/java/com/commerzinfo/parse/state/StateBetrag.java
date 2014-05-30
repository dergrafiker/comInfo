package com.commerzinfo.parse.state;

import com.commerzinfo.parse.ParseStateContext;
import com.commerzinfo.util.DecimalFormatUtil;
import com.commerzinfo.util.RegexUtil;

public class StateBetrag extends ParseState {
    public StateBetrag() {
        super(StateBetrag.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return RegexUtil.matchesAmount(input);
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws Exception {
        stateContext.getBuchungszeile().setValue(DecimalFormatUtil.parse(input).doubleValue());
        stateContext.setState(ParseStateContext.INITIAL_STATE);
    }
}
