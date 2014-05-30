package com.commerzinfo.parse.state;

import com.commerzinfo.parse.ParseStateContext;

public abstract class ParseState {
    private String stateName;

    protected ParseState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    public abstract boolean checkCondition(String input);

    public abstract void doSomething(ParseStateContext stateContext, String input) throws Exception;

    public void processInput(ParseStateContext stateContext, String input) {
        try {
            if (checkCondition(input)) {
                doSomething(stateContext, input);
            } else {
                stateContext.resetToInitialState();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
