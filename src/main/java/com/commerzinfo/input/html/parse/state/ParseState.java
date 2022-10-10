package com.commerzinfo.input.html.parse.state;

import com.commerzinfo.input.html.parse.ParseStateContext;

import java.text.ParseException;

public abstract class ParseState {
    private final String stateName;

    ParseState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    protected abstract boolean checkCondition(String input);

    protected abstract void doSomething(ParseStateContext stateContext, String input) throws ParseException;

    public void processInput(ParseStateContext stateContext, String input) {
        try {
            if (checkCondition(input)) {
                doSomething(stateContext, input);
            } else {
                stateContext.resetToInitialState();
            }
        } catch (Exception e) {
            throw new ParsingFailedException(e);
        }
    }

    public static class ParsingFailedException extends RuntimeException {
        public ParsingFailedException(Throwable cause) {
            super(cause);
        }
    }
}
