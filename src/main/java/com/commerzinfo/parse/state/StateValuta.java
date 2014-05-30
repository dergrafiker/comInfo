package com.commerzinfo.parse.state;

import com.commerzinfo.parse.ParseStateContext;
import com.commerzinfo.util.DateUtil;
import com.commerzinfo.util.RegexUtil;

public class StateValuta extends ParseState {
    public StateValuta() {
        super(StateValuta.class.getSimpleName());
    }

    @Override
    public boolean checkCondition(String input) {
        return RegexUtil.matchesDate(input);
    }

    @Override
    public void doSomething(ParseStateContext stateContext, String input) throws Exception {
        stateContext.getBuchungszeile().setValueDate(DateUtil.parse(input));
        stateContext.setState(ParseStateContext.BETRAG_STATE);
    }
}
