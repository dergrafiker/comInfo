package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.input.html.parse.ParseStateContext;
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
        stateContext.getBuchungszeile().setValue((java.math.BigDecimal) DecimalFormatUtil.parse(input, DecimalFormatUtil.Mode.HTML));
        stateContext.setState(ParseStateContext.INITIAL_STATE);
    }
}
